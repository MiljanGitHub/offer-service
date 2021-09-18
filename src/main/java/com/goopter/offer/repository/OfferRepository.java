package com.goopter.offer.repository;

import com.goopter.offer.model.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {

    List<Offer> findByItemId(String itemId);
    Offer findByTransactionId(String transactionId);

}
