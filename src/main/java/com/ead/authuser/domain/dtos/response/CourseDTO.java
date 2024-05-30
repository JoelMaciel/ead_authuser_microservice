package com.ead.authuser.domain.dtos.response;

import com.ead.authuser.domain.enums.CourseLevel;
import com.ead.authuser.domain.enums.CourseStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseDTO {

    private UUID courseId;
    private String name;
    private String description;
    private CourseStatus courseStatus;
    private CourseLevel courseLevel;
    private UUID userInstructor;
    private String imageUrl;

}
