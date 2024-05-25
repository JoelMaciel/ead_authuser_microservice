package com.ead.authuser.domain.dtos.request;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateImageRequestDTO {

    @NotBlank
    private String imageUrl;
}
