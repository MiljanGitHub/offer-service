package com.goopter.offer.service;

import com.goopter.offer.dto.GenericResponse;
import com.goopter.offer.dto.OfferDto;
import com.goopter.offer.dto.OfferDtoResponse;
import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.exception.ResourceNotFoundException;

public interface OfferService {

    GenericResponse createOffer(OfferDto offerDto, String language);
    OfferDto getAllowedEvents(String offerId, String language) throws ResourceNotFoundException;
    OfferDtoResponse getOffers(String itemId, String language);
    String instantAcceptOffer(String offerId, String language) throws Exception;
    String acceptOffer(String offerId, String language) throws ResourceNotFoundException, ErrorException;
    String declineBySeller(String offerId, String language) throws ErrorException;
    String sentBySeller(String offerId, String language) throws ErrorException;
    String carrierDelivered(String offerId, String language) throws ErrorException;

    /**
        Only for testing purposes
    */
    void test();


}
