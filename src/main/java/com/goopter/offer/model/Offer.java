package com.goopter.offer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "offer")
public class Offer {

    @Id
    private String offerId;
    private int buyerId;
    private int sellerId;
    private long amount;
    private String itemId;
    private boolean accepted;
    private String transactionId;
    private List<State> states;

}
