package com.ead.authuser.domain.exceptions;

public class PasswordMismatchedException extends BusinessException {
    public PasswordMismatchedException(String message) {
        super(message);
    }
}
