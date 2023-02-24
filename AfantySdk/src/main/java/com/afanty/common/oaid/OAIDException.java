package com.afanty.common.oaid;

public class OAIDException extends RuntimeException{
    public OAIDException(String message) {
        super(message);
    }

    public OAIDException(Throwable cause) {
        super(cause);
    }
}
