package com.goopter.offer.sm;

import com.goopter.offer.util.AppConstant;
import com.goopter.offer.model.*;
import com.goopter.offer.repository.OfferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class OfferChangeStateInterceptor extends StateMachineInterceptorAdapter<OfferState, OfferEvent> {

    private OfferRepository offerRepository;

    @Override
    public void preStateChange(State<OfferState, OfferEvent> state, Message<OfferEvent> message, Transition<OfferState, OfferEvent> transition, StateMachine<OfferState, OfferEvent> stateMachine) {

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")))
                .ifPresent(offerId -> {
                    log.debug( String.format("Entered OfferChangeStateInterceptor with state: %s +  for Offer %s", state.getId().toString(), offerId));

                    Optional<Offer> o = offerRepository.findById(offerId);

                    if (o.isPresent()){

                        com.goopter.offer.model.State s = com.goopter.offer.model.State.builder()
                                .offerState(state.getId())
                                .created(String.valueOf(System.currentTimeMillis()))
                                .build();

                        o.get().getStates().add(s);

                        offerRepository.save(o.get());

                        boolean isFortyEightHoursHold = (boolean) message.getHeaders().getOrDefault(AppConstant.FORTY_EIGHT_HOURS_HOLD, false);

                        if(isFortyEightHoursHold){

                            Runnable runnable = () -> {
                               long fortyEightHoursMills = 172800000L;
                                String oId = offerId;
                                try {
                                    Thread.sleep(fortyEightHoursMills);

                                     Offer offer = offerRepository.findById(oId).get();
                                     int size = offer.getStates().size();
                                     OfferState os = offer.getStates().get((size > 0 ? size : 1) - 1).getOfferState();
                                     if (!os.name().equalsIgnoreCase(OfferState.REVOKED.name())){
                                         //This means that Buyer haven't contacted offer-service within 48 hours to say that something is wrong with his Item, so we can set Offer's last state to OFFER_OK
                                         com.goopter.offer.model.State offerOkState = com.goopter.offer.model.State.builder()
                                                 .offerState(OfferState.OFFER_OK)
                                                 .created(String.valueOf(System.currentTimeMillis()))
                                                 .build();
                                         offer.getStates().add(offerOkState);

                                         offerRepository.save(offer);

                                         //TODO Inform message-service that Buyer has agreed to the delivered Item and he has not made any complaints within 48h

                                         //TODO OfferUp must also contact the Seller's bank so bank can initiate process of PaymentReservation;
                                         // this service must return some kind of transaction identifier so we can associate it with the Offer
                                     }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();

                                }

                            };
                            new Thread(runnable).start();

                        }




                    } else {
                        //TODO: notify client of the error
                    }
                });
    }
}
