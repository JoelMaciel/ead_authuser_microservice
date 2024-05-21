package com.ead.authuser.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String cpf;
    private String imageUrl;
}
