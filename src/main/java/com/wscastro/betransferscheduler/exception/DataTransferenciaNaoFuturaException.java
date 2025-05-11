package com.wscastro.betransferscheduler.exception;

public class DataTransferenciaNaoFuturaException extends RuntimeException {
    public DataTransferenciaNaoFuturaException(String message) {
        super(message);
    }
}