package com.ead.authuser.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdatePasswordRequestDTO {

    private String password;
    private String oldPassword;
}
