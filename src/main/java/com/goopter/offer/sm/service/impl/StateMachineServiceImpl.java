package com.goopter.offer.sm.service.impl;

import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.model.Offer;
import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import com.goopter.offer.sm.OfferChangeStateInterceptor;
import com.goopter.offer.sm.service.StateMachineService;
import com.goopter.offer.util.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
@AllArgsConstructor
public class StateMachineServiceImpl implements StateMachineService {

    private final MessageSource messageSource;
    private final OfferChangeStateInterceptor offerChangeStateInterceptor;
    private final StateMachineFactory<OfferState, OfferEvent> stateMachineFactory;

    @Override
    public StateMachine<OfferState, OfferEvent> build(Offer offer) {
        StateMachine<OfferState, OfferEvent> sm = stateMachineFactory.getStateMachine(offer.getOfferId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(offerChangeStateInterceptor);
                    int size = offer.getStates().size();
                    OfferState os = offer.getStates().get((size > 0 ? size : 1) - 1).getOfferState();
                    sma.resetStateMachine(new DefaultStateMachineContext<>(os, null, null, null));
                });

        sm.start();

        return sm;
    }

    @Override
    public void sendEvent(String offerId, StateMachine<OfferState, OfferEvent> sm, OfferEvent offerEvent) {
        Message msg = MessageBuilder.withPayload(offerEvent)
                .setHeader(AppConstant.OFFER_ID_HEADER, offerId)
                .build();

        sm.sendEvent(msg);
    }

    @Override
    public void checkStateMachineErrors(StateMachine<OfferState, OfferEvent> sm, String language) throws ErrorException {
        if (sm.hasStateMachineError()){
            ErrorException error = (ErrorException) sm.getExtendedState().getVariables().get(AppConstant.EXCEPTION);
            error.setMessage(generateMessage(error.getMessage(), language)); throw error;
        }
    }

    private String generateMessage(String messageKey, String language){
        return messageSource.getMessage(messageKey, null, new Locale(language));
    }
}
