package com.example.planning.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(final Throwable cause) {
        super(cause);
    }

    public ItemNotFoundException(final String message) {
        super(message);
    }
}
