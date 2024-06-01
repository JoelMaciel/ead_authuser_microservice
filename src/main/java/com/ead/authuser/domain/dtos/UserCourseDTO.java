package com.ead.authuser.domain.dtos;

import com.ead.authuser.domain.dtos.response.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCourseDTO {

    private UUID id;
    private UUID courseId;
    private UserDTO user;
}
