package com.goopter.offer.service.impl;

import com.goopter.offer.mapper.OfferMapper;
import com.goopter.offer.model.Offer;
import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import com.goopter.offer.repository.OfferRepository;
import com.goopter.offer.service.TransactionService;
import com.goopter.offer.sm.OfferChangeStateInterceptor;
import com.goopter.offer.sm.service.StateMachineService;
import com.goopter.offer.util.AppConstant;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final OfferMapper offerMapper;
    private final MessageSource messageSource;
    private final OfferRepository offerRepository;
    private final StateMachineService stateMachineService;


    @Override
    public String paymentReservation(String transactionId, String language) {
        Offer offer = offerRepository.findByTransactionId(transactionId);

        if (offer == null ){
            //TODO Inform admin that there was an error with bank-processing particular Offer
            return AppConstant.ACKNOWLEDGED;
        }

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(offer);

        stateMachineService.sendEvent(offer.getOfferId(), sm, OfferEvent.PAYMENT_RESERVATION);

        return AppConstant.ACKNOWLEDGED;
    }

    @Override
    public String paymentDeposited(String transactionId, String language) {
        Offer offer = offerRepository.findByTransactionId(transactionId);

        if (offer == null ){
            //TODO Inform admin that there was an error with bank-processing particular Offer
            return AppConstant.ACKNOWLEDGED;
        }

        StateMachine<OfferState, OfferEvent> sm = stateMachineService.build(offer);

        stateMachineService.sendEvent(offer.getOfferId(), sm, OfferEvent.PAYMENT_TRANSFERRED);

        return AppConstant.ACKNOWLEDGED;

    }
}
