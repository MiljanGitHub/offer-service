package com.goopter.offer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@AllArgsConstructor
@RestControllerAdvice
public class ExceptionHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<String> customException(final Exception ex){

        return ResponseEntity.ok().body(ex.getMessage());
    }

}
