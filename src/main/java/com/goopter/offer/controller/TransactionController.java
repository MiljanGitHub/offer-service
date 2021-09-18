package com.goopter.offer.controller;

import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.exception.ResourceNotFoundException;
import com.goopter.offer.service.OfferService;
import com.goopter.offer.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private TransactionService transactionService;

    @PostMapping("/payment-reservation/{transactionId}")
    @ApiOperation(value = "Payment reservation for given Offer entity.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "You have successfully notified us about given transaction."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> paymentReservation(@PathVariable("transactionId") String transactionId, @RequestHeader String language) throws ResourceNotFoundException, ErrorException {
        log.info("Payment reservation --- " + transactionId);
        String response = transactionService.paymentReservation(transactionId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/payment-deposited/{transactionId}")
    @ApiOperation(value = "Payment deposited for given Offer entity.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "You have successfully notified us about given transaction."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> paymentDeposited(@PathVariable("transactionId") String transactionId, @RequestHeader String language) throws ResourceNotFoundException, ErrorException {
        log.info("Payment deposited --- " + transactionId);
        String response = transactionService.paymentDeposited(transactionId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
