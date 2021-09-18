package com.goopter.offer.sm;

import com.goopter.offer.model.OfferEvent;
import com.goopter.offer.model.OfferState;
import com.goopter.offer.sm.actions.*;
import com.goopter.offer.sm.guards.AcceptOfferGuard;
import com.goopter.offer.sm.guards.BuyerDeclinedGuard;
import com.goopter.offer.sm.guards.InstantAcceptOfferGuard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import java.util.EnumSet;


@Slf4j
@Configuration
@AllArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OfferState, OfferEvent> {

    private final AcceptOfferGuard acceptOfferGuard;
    private final AcceptOfferAction acceptOfferAction;

    private final InstantAcceptAction instantAcceptOfferAction;
    private final InstantAcceptOfferGuard instantAcceptOfferGuard;

    private final DeclineSellerAction declineSellerAction;

    private final SellerSentAction sellerSentAction;

    private final BuyerDeclinedAction buyerDeclinedAction;
    private final BuyerDeclinedGuard buyerDeclinedGuard;

    private final CarrierDeliveredAction carrierDeliveredAction;

    private final PaymentReservationAction paymentReservationAction;

    private final PaymentDepositedAction paymentDepositedAction;

    private static boolean evaluate(StateContext<OfferState, OfferEvent> context) {
        return true;
    }

    @Override
    public void configure(StateMachineStateConfigurer<OfferState, OfferEvent> states) throws Exception {
        states.withStates()
                .initial(OfferState.PENDING)
                .states(EnumSet.allOf(OfferState.class))
                .end(OfferState.DECLINED)
                .end(OfferState.REVOKED)
                .end(OfferState.DEPOSITED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OfferState, OfferEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(OfferState.PENDING).target(OfferState.ACCEPTED).event(OfferEvent.INSTANT_ACCEPT)
                .action(instantAcceptOfferAction).guard(instantAcceptOfferGuard)
                .and()
                .withExternal().source(OfferState.ACCEPTED).target(OfferState.DECLINED).event(OfferEvent.SELLER_DECLINED)
                .action(declineSellerAction)
                .and()
                .withExternal().source(OfferState.PENDING).target(OfferState.AUTH).event(OfferEvent.ACCEPT_OFFER)
                .action(acceptOfferAction).guard(acceptOfferGuard)
                .and()
                .withExternal().source(OfferState.AUTH).target(OfferState.ITEM_SENT).event(OfferEvent.SELLER_SENT)
                .action(sellerSentAction)
                .and()
                .withExternal().source(OfferState.AUTH).target(OfferState.DELIVERED).event(OfferEvent.CARRIER_DELIVERED)
                .action(carrierDeliveredAction)
                .and()
                .withExternal().source(OfferState.ITEM_SENT).target(OfferState.DELIVERED).event(OfferEvent.CARRIER_DELIVERED)
                .action(carrierDeliveredAction)
                .and()
                .withExternal().source(OfferState.DELIVERED).target(OfferState.REVOKED).event(OfferEvent.BUYER_DECLINED)
                .action(buyerDeclinedAction).guard(buyerDeclinedGuard)
                .and()
                .withExternal().source(OfferState.OFFER_OK).target(OfferState.PAYMENT_SCHEDULED).event(OfferEvent.PAYMENT_RESERVATION)
                .action(paymentReservationAction)
                .and()
                .withExternal().source(OfferState.PAYMENT_SCHEDULED).target(OfferState.DEPOSITED).event(OfferEvent.PAYMENT_TRANSFERRED)
                .action(paymentDepositedAction);
    }


}















