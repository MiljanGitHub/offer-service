package com.goopter.offer.service;

public interface TransactionService {
    String paymentReservation(String transactionId, String language);
    String paymentDeposited(String transactionId, String language);
}
