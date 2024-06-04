package com.ead.authuser.domain.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEventDTO {

    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private String userStatus;
    private String userType;
    private String cpf;
    private String actionType;
}
