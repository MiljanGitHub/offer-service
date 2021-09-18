package com.goopter.offer.dto;

import com.goopter.offer.model.OfferEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.mapstruct.Mapper;

import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {

    private int buyerId;
    private int sellerId;
    private int itemId;
    private long amount;
    private String item;
    private String status;
    private List<OfferEvent> allowedEvents;

}
