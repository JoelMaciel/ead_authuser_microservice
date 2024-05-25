package com.ead.authuser.domain.dtos.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdatePasswordRequestDTO {

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    private String oldPassword;
}
