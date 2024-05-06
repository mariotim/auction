package de.dbauction.auction.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(AuctionClientErrorException.class)
    ResponseEntity clientError(AuctionClientErrorException ex) {
        return badRequest().body(ex.getMessage());
    }
}