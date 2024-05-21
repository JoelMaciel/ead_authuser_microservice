package com.ead.authuser.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    @Size(min = 6, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(min = 10, max = 150)
    private String fullName;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @CPF
    private String cpf;

}
