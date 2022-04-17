package com.example.planning.exception;

public class ApplicationException extends RuntimeException {

    public ApplicationException() {
        super();
    }

    public ApplicationException(final Throwable cause) {
        super(cause);
    }

    public ApplicationException(final String message) {
        super(message);
    }
}
