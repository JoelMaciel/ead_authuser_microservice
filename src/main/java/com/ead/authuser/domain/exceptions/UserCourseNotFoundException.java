package com.ead.authuser.domain.exceptions;

import java.util.UUID;


public class UserCourseNotFoundException extends EntityNotFoundException {

    public UserCourseNotFoundException(String message) {
        super(message);
    }

    public UserCourseNotFoundException(UUID courseId) {
        this(String.format("UserCourse not found for this id %s", courseId));
    }
}
