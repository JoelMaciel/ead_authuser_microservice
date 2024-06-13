package com.ead.authuser.domain.exceptions;

public class RoleNotFoundException extends EntityNotFoundException{

    public RoleNotFoundException(String message) {
        super(message);
    }
}
