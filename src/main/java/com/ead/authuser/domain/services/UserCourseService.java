package com.ead.authuser.domain.services;

import com.ead.authuser.domain.dtos.UserCourseDTO;
import com.ead.authuser.domain.dtos.request.UserCourseRequestDTO;
import com.ead.authuser.domain.models.UserModel;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;

public interface UserCourseService {

    UserCourseDTO save(UUID userId, UserCourseRequestDTO userCourseRequestDTO);

    boolean existsByUserAndCourseId(UserModel user, UUID courseId);
}
