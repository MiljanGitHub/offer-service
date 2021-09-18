package com.goopter.offer.sm.guards;

import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.model.Offer;
import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import com.goopter.offer.repository.OfferRepository;
import com.goopter.offer.util.AppConstant;
import lombok.AllArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@Component
@AllArgsConstructor
public class AcceptOfferGuard implements Guard<OfferState, OfferEvent> {

    private final OfferRepository offerRepository;
    //private WebClient webClient;

    @Override
    public boolean evaluate(StateContext<OfferState, OfferEvent> stateContext) {
        final boolean[] guardOk = new boolean[1]; Arrays.fill(guardOk, Boolean.TRUE);

        Optional.ofNullable(stateContext.getMessage())
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")))
                .ifPresentOrElse(offerId -> {

                    Optional<Offer> o = offerRepository.findById(offerId);

                    if (o.isPresent()){
                        //Buyer's card must have enough financial assets.

                        if (!moneyCheckOk(o.get())){
                            setMessageError("not.enough.assets2", stateContext);

                            //TODO Inform buyer via message-service that his offer was declined
                            //use this message key: not.enough.assets1

                            progressOfferToDeclinedState(o.get());
                            guardOk[0] = false;
                        }

                    }

                }, () -> {
                    guardOk[0] = false;
                    setMessageError("bad.id", stateContext);
                });

        return guardOk[0];
    }


    private boolean moneyCheckOk(Offer offer){

        //TODO make an API call to item-service to fetch Buyer's bank data
        //TODO make an API call to Buyer's bank to check if there's enough financial assets, including OfferUp's interest

        return true;
    }

    private void setMessageError(String messageKey, StateContext<OfferState, OfferEvent> stateContext){
        Exception ex = new ErrorException(messageKey);
        stateContext.getStateMachine().setStateMachineError(ex);
        stateContext.getStateMachine().getExtendedState().getVariables().put(AppConstant.EXCEPTION, ex);
    }

    private void progressOfferToDeclinedState(Offer offer){
        com.goopter.offer.model.State s = com.goopter.offer.model.State.builder()
                .offerState(OfferState.DECLINED)
                .created(String.valueOf(System.currentTimeMillis()))
                .build();
        offer.getStates().add(s);
        offerRepository.save(offer);
    }
}
