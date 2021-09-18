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


@Component
@AllArgsConstructor
public class InstantAcceptAction implements Action<OfferState, OfferEvent> {

    private final OfferRepository offerRepository;

    @Override
    public void execute(StateContext<OfferState, OfferEvent> stateContext) {

        //We checked offerId existence in the InstantAcceptOfferGuard
        Offer o = offerRepository.findById((String) stateContext.getMessage().getHeaders().getOrDefault(AppConstant.OFFER_ID_HEADER, "-1")).get();

        //TODO inform buyer and seller, via message-service, that Offer has been accepted. They can meetup live and do the exchange.

        //TODO inform item-service to mark Item as 'sold' i.e. non-active

    }
}
