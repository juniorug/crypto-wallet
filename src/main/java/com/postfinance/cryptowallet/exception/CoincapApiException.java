package com.postfinance.cryptowallet.exception;

public class CoincapApiException extends RuntimeException {
    public CoincapApiException(String message, Throwable cause) {
        super(message, cause);
    }
}