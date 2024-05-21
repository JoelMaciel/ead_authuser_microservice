package com.ead.authuser.domain.exceptions;

import java.util.UUID;


public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(UUID userId) {
        this(String.format("User not found for this userId %s", userId));
    }
}
