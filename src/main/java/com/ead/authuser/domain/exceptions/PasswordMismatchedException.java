package com.ead.authuser.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordMismatchedException extends RuntimeException{
    public PasswordMismatchedException(String message) {
        super(message);
    }
}
