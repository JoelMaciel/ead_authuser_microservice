package com.ead.authuser.domain.exceptions;

public class EmailAlreadyExistsException extends EntityInUseException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
