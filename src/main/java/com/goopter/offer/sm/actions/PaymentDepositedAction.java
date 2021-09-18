package com.goopter.offer.sm.actions;

import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.model.Offer;
import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import com.goopter.offer.repository.OfferRepository;
import com.goopter.offer.util.AppConstant;
import lombok.AllArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PaymentDepositedAction implements Action<OfferState, OfferEvent> {

    private final OfferRepository offerRepository;

    @Override
    public void execute(StateContext<OfferState, OfferEvent> stateContext) {

        Optional.ofNullable(stateContext.getMessage())
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")))
                .ifPresentOrElse(offerId -> {

                            Optional<Offer> o = offerRepository.findById(offerId);

                            if (o.isPresent()){
                                //TODO inform message-service that Seller's bank has processed his payment and he can see it on his bank account

                            } else setMessageError("bad.id", stateContext);

                        }, () -> setMessageError("bad.id", stateContext)
                );

    }
    private void setMessageError(String messageKey, StateContext<OfferState, OfferEvent> stateContext){
        Exception ex = new ErrorException(messageKey);
        stateContext.getStateMachine().setStateMachineError(ex);
        stateContext.getStateMachine().getExtendedState().getVariables().put(AppConstant.EXCEPTION, ex);
    }
}
