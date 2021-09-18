package com.goopter.offer.sm.actions;

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
public class AcceptOfferAction implements Action<OfferState, OfferEvent> {

    private final OfferRepository offerRepository;

    @Override
    public void execute(StateContext<OfferState, OfferEvent> stateContext) {

        //We've checked offerId existence in the AcceptOfferGuard
        Offer o = offerRepository.findById((String) stateContext.getMessage().getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")).get();

        //TODO
        //Deduct from Buyer's card price of the Item + OfferUp's interest.
        //Inform Buyer and Seller about taken actions


    }
}
