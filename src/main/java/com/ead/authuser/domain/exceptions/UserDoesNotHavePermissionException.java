package com.ead.authuser.domain.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class UserDoesNotHavePermissionException extends AccessDeniedException {

    public UserDoesNotHavePermissionException(String message) {
        super(message);
    }
}
