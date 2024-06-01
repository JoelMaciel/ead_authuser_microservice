package com.ead.authuser.domain.dtos.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UserCourseRequestDTO {

    private UUID userId;

    @NotNull
    private UUID courseId;
}
