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

@Component
@AllArgsConstructor
public class BuyerDeclinedGuard implements Guard<OfferState, OfferEvent> {

    private final OfferRepository offerRepository;

    @Override
    public boolean evaluate(StateContext<OfferState, OfferEvent> stateContext) {
        final boolean[] guardOk = new boolean[1]; Arrays.fill(guardOk, Boolean.TRUE);

        Optional.ofNullable(stateContext.getMessage())
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")))
                .ifPresentOrElse(offerId -> {

                    Optional<Offer> o = offerRepository.findById(offerId);

                    if (o.isPresent()){

                        //TODO Buyer can inform the system that Item he received is not as expected within 48h since it has been delivered to him

                        int size = o.get().getStates().size();
                        String createdAt = o.get().getStates().get((size > 0 ? size : 1) - 1).getCreated();
                        boolean isAfter48hours = (System.currentTimeMillis() - Long.valueOf(createdAt)) > 172800000;
                        if(isAfter48hours){
                            guardOk[0] = false;
                            setMessageError("cannot.revoke", stateContext);
                        }
                    }

                }, () -> {
                    guardOk[0] = false;
                    setMessageError("bad.id", stateContext);
                });

        return guardOk[0];
    }

    private void setMessageError(String messageKey, StateContext<OfferState, OfferEvent> stateContext){
        Exception ex = new ErrorException(messageKey);
        stateContext.getStateMachine().setStateMachineError(ex);
        stateContext.getStateMachine().getExtendedState().getVariables().put(AppConstant.EXCEPTION, ex);
    }
}
