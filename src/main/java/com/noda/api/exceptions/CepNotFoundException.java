package com.noda.api.exceptions;

public class CepNotFoundException extends RuntimeException {
    public CepNotFoundException(String message) {
        super(message);
    }
}
