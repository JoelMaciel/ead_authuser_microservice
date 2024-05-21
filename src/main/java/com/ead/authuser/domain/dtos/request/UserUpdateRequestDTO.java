package com.ead.authuser.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDTO {

    private String username;
    private String fullName;
    private String phoneNumber;
}
