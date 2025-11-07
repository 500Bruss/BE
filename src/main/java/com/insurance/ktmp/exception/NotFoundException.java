package com.insurance.ktmp.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }
}
