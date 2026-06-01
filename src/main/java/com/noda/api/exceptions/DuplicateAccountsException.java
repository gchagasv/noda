package com.noda.api.exceptions;

public class DuplicateAccountsException extends RuntimeException {
    public DuplicateAccountsException(String message) {
        super(message);
    }
}
