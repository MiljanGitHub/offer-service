package com.goopter.offer.controller;

import com.goopter.offer.dto.GenericResponse;
import com.goopter.offer.dto.OfferDto;
import com.goopter.offer.dto.OfferDtoResponse;
import com.goopter.offer.exception.ErrorException;
import com.goopter.offer.exception.ResourceNotFoundException;
import com.goopter.offer.service.OfferService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.PushBuilder;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/offer")
public class OfferController {

    private OfferService offerService;

    @PostMapping("/create")
    @ApiOperation(value = "Creates a new Offer entity.")
    @ApiResponses(
            value = {
                  @ApiResponse(code = 200, message = "Successfully created new Offer."),
                  @ApiResponse(code = 401, message = "You are not authorized for this service."),
                  @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                  @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<GenericResponse> createOffer(@RequestBody OfferDto offerDto, @RequestHeader String language){
        log.info("Creating offer --- " + offerDto);

        GenericResponse response = offerService.createOffer(offerDto, language);

        return ResponseEntity.status(HttpStatus.valueOf(response.getHttpCode())).body(response);
    }

    @PutMapping("/instant-accept/{offerId}")
    @ApiOperation(value = "Instantly accepts Offer entity.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully accepted instant Offer."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> instantAcceptOffer(@PathVariable("offerId") String offerId, @RequestHeader String language) throws Exception {
        log.info("Instant accepting offer --- " + offerId);
        String response = offerService.instantAcceptOffer(offerId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/accept/{offerId}")
    @ApiOperation(value = "Accepts Offer entity.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully accepted Offer."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> acceptOffer(@PathVariable("offerId") String offerId, @RequestHeader String language) throws ResourceNotFoundException, ErrorException {
        log.info("Accepting offer --- " + offerId);
        String response = offerService.acceptOffer(offerId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/decline/{offerId}")
    @ApiOperation(value = "Decline Offer entity.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully declined Offer."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> declineBySeller(@PathVariable("offerId") String offerId, @RequestHeader String language) throws ResourceNotFoundException, ErrorException {
        log.info("Declining offer by Seller --- " + offerId);
        String response = offerService.declineBySeller(offerId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/sent/{offerId}")
    @ApiOperation(value = "Sent Item as part of response to submitted Offer entity.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully sent Item."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> sentBySeller(@PathVariable("offerId") String offerId, @RequestHeader String language) throws ResourceNotFoundException, ErrorException {
        log.info("Sent event by Seller --- " + offerId);
        String response = offerService.sentBySeller(offerId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/carrier-delivered/{offerId}")
    @ApiOperation(value = "Service for carrier postal services to confirm delivery of Item.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully delivered Item to buyer."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<String> carrierDelivered(@PathVariable("offerId") String offerId, @RequestHeader String language) throws ResourceNotFoundException, ErrorException {
        log.info("Delivered Item to Buyer --- " + offerId);
        String response = offerService.carrierDelivered(offerId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/allowedEvents/{offerId}")
    @ApiOperation(value = "Fetches Offer DTO and offer's allowed to be called.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Fetched allowed events to be invoked on Offer"),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<OfferDto> getAllowedEvents(@PathVariable("offerId") String offerId, @RequestHeader String language) throws ResourceNotFoundException {
        log.info("Getting allowed events for the State Machine --- " + offerId);
        OfferDto response = offerService.getAllowedEvents(offerId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{itemId}")
    @ApiOperation(value = "Fetches Offer DTOs for a given item id.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Fetches Offer DTOs."),
                    @ApiResponse(code = 401, message = "You are not authorized for this service."),
                    @ApiResponse(code = 403, message = "Service you are trying to reach is forbidden."),
                    @ApiResponse(code = 500, message = "Internal server error. Please report to the Engineering team.")
            }
    )
    public ResponseEntity<OfferDtoResponse> getOffers(@PathVariable("itemId") String itemId, @RequestHeader String language){
        log.info("Getting offers for a given Item --- " + itemId);
        OfferDtoResponse response = offerService.getOffers(itemId, language);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /**
        Only for testing purposes.
     */
    @GetMapping("/test")
    public String test(){
        offerService.test();
        return "ok";
    }


}
