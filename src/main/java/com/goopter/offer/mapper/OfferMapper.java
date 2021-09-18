package com.goopter.offer.mapper;

import com.goopter.offer.dto.OfferDto;
import com.goopter.offer.model.Offer;
import org.mapstruct.Mapper;

@Mapper
public interface OfferMapper {

    Offer dtoToOffer(OfferDto offerDto);
    OfferDto offerToDto(Offer offer);

}
