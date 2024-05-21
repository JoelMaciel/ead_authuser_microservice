package com.ead.authuser.domain.exceptions;

public class UsernameAlreadyExistsException extends EntityInUseException {

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
