package com.goopter.offer.sm.service;

import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.model.Offer;
import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import org.springframework.statemachine.StateMachine;

public interface StateMachineService {

    StateMachine<OfferState, OfferEvent> build(Offer offer);
    void sendEvent(String offerId, StateMachine<OfferState, OfferEvent> sm, OfferEvent offerEvent);
    void checkStateMachineErrors(StateMachine<OfferState, OfferEvent> sm, String language) throws ErrorException;

}
