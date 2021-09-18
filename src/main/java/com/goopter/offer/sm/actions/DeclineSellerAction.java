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
public class DeclineSellerAction implements Action<OfferState, OfferEvent> {

    private final OfferRepository offerRepository;

    @Override
    public void execute(StateContext<OfferState, OfferEvent> stateContext) {

        Optional.ofNullable(stateContext.getMessage())
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")))
                .ifPresentOrElse(offerId -> {

                    Optional<Offer> o = offerRepository.findById(offerId);

                    if (o.isPresent()){

                        progressOfferToDeclinedState(o.get());

                        //TODO
                        //Inform Buyer that his offer was declined by Seller

                        //TODO
                        //Inform item-service that Item is again 'active' i.e. can be again placed in active Items
                    }

                }, () -> setMessageError("bad.id", stateContext)
                );
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
