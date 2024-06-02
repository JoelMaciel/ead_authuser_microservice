package com.ead.authuser.domain.exceptions;

public class SubscriptionAlreadyExistsException extends EntityInUseException {

    public SubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
