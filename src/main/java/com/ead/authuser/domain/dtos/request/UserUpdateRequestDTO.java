package com.ead.authuser.domain.dtos.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateRequestDTO {

    @NotBlank
    @Size(min = 6, max = 50)
    private String username;

    @NotBlank
    @Size(min = 10, max = 150)
    private String fullName;

    @NotBlank
    private String phoneNumber;
}
