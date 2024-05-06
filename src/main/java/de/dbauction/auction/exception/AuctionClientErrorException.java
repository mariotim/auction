package de.dbauction.auction.exception;

public class AuctionClientErrorException extends RuntimeException{
    public static final String XX_CLIENT_ERROR = "4xx Client Error";
    public AuctionClientErrorException(String clientErrorOccurred) {
        super(clientErrorOccurred);
    }
}
