package com.goopter.offer.service.impl;

import com.goopter.offer.dto.OfferDtoResponse;
import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.exception.ResourceNotFoundException;
import com.goopter.offer.sm.service.StateMachineService;
import com.goopter.offer.util.AppConstant;
import com.goopter.offer.dto.GenericResponse;
import com.goopter.offer.dto.OfferDto;
import com.goopter.offer.mapper.OfferMapper;
import com.goopter.offer.model.Offer;
import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import com.goopter.offer.model.State;
import com.goopter.offer.repository.OfferRepository;
import com.goopter.offer.service.OfferService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferMapper offerMapper;
    private final MessageSource messageSource;
    private final OfferRepository offerRepository;
    private final StateMachineService stateMachineService;

    @Override
    public GenericResponse createOffer(OfferDto offerDto, String language) {

        //TODO make service call to items-service to get hold of Item's price and compare Offer's price with Item's price
        //Offer's price must not be negative value
        double itemPrice = new Random().nextDouble();

        if (itemPrice < 0.000) {
            //throw some kind of exception
            //use this message:
        }
        boolean offeredPriceOk = itemPrice <= offerDto.getAmount();
        if (!offeredPriceOk) {
            //throw some kind of exception for Buyer who is making an Offer
            //use this message: less.than.zero
            //Seller doesn't have to be informed.
        }
        Offer offer = offerMapper.dtoToOffer(offerDto);
        offer.setStates(List.of(State.builder()
                .created(String.valueOf(System.currentTimeMillis()))
                .offerState(OfferState.PENDING)
                .build()));
        offer = offerRepository.save(offer);

        offer.setStates(List.of(State.builder()
                .created(String.valueOf(System.currentTimeMillis()))
                .offerState(OfferState.PENDING)
                .build()));

        return GenericResponse.builder()
                .id(String.valueOf(offer.getOfferId()))
                .httpCode(HttpStatus.OK.value())
                .message(generateMessage("success", language))
                .build();
    }

    @Override
    public OfferDto getAllowedEvents(String offerId, String language) throws ResourceNotFoundException {

        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if (optionalOffer.isEmpty()) throw new ResourceNotFoundException(generateMessage("bad.id", language));

        int size = optionalOffer.get().getStates().size();
        OfferState lastState = optionalOffer.get().getStates().get((size > 0 ? size : 1) - 1).getOfferState();
        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(optionalOffer.get());

        List<OfferEvent> allowedEvents = sm.getTransitions().stream()
                .filter(transition -> transition.getSource().getId().toString().equalsIgnoreCase(lastState.toString()))
                .map(transition -> transition.getTrigger().getEvent())
                .collect(Collectors.toUnmodifiableList());
        OfferDto dto = offerMapper.offerToDto(optionalOffer.get());

        dto.setAllowedEvents(allowedEvents);

        return dto;
    }

    @Override
    public OfferDtoResponse getOffers(String itemId, String language) {

        return OfferDtoResponse.builder().offers(offerRepository.findByItemId(itemId).stream()
                .map(offerMapper::offerToDto)
                .collect(Collectors.toUnmodifiableList())).build();
    }

    @Override
    public String instantAcceptOffer(String offerId, String language) throws Exception {

        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if (optionalOffer.isEmpty()) throw new ResourceNotFoundException(generateMessage("bad.id", language));

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(optionalOffer.get());

        stateMachineService.sendEvent(offerId, sm, OfferEvent.INSTANT_ACCEPT);

        stateMachineService.checkStateMachineErrors(sm, language);

        return AppConstant.SUCCESSFULLY_ACCEPTED;
    }

    @Override
    public String acceptOffer(String offerId, String language) throws ResourceNotFoundException, ErrorException {

        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if (optionalOffer.isEmpty()) throw new ResourceNotFoundException(generateMessage("bad.id", language));

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(optionalOffer.get());

        stateMachineService.sendEvent(offerId, sm, OfferEvent.ACCEPT_OFFER);

        stateMachineService.checkStateMachineErrors(sm, language);

        return AppConstant.OFFER_ACCEPTED;
    }

    @Override
    public String declineBySeller(String offerId, String language) throws ErrorException {

        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if (optionalOffer.isEmpty()) throw new ErrorException(generateMessage("bad.id", language));

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(optionalOffer.get());

        stateMachineService.sendEvent(offerId, sm, OfferEvent.SELLER_DECLINED);

        stateMachineService.checkStateMachineErrors(sm, language);

        return AppConstant.DECLINED_SUCCESSFULLY;
    }

    @Override
    public String sentBySeller(String offerId, String language) throws ErrorException {

        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if (optionalOffer.isEmpty()) throw new ErrorException(generateMessage("bad.id", language));

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(optionalOffer.get());

        stateMachineService.sendEvent(offerId, sm, OfferEvent.SELLER_SENT);

        stateMachineService.checkStateMachineErrors(sm, language);

        return AppConstant.SUCCESSFULLY_SENT;
    }

    @Override
    public String carrierDelivered(String offerId, String language) throws ErrorException {
        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if (optionalOffer.isEmpty()) throw new ErrorException(generateMessage("bad.id", language));

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(optionalOffer.get());

        stateMachineService.sendEvent(offerId, sm, OfferEvent.CARRIER_DELIVERED);

        stateMachineService.checkStateMachineErrors(sm, language);

        return AppConstant.ACKNOWLEDGED;
    }


    @Override
    public void test() {


    }


    private String generateMessage(String messageKey, String language) {
        return messageSource.getMessage(messageKey, null, new Locale(language));
    }
}