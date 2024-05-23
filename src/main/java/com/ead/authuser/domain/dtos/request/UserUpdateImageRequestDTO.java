package com.ead.authuser.domain.dtos.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
public class UserUpdateImageRequestDTO {

    @NotBlank
    private String imageUrl;
}
