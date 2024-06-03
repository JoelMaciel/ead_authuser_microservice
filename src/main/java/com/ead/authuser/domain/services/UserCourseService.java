package com.ead.authuser.domain.services;

import com.ead.authuser.domain.dtos.UserCourseDTO;
import com.ead.authuser.domain.dtos.request.UserCourseRequestDTO;
import com.ead.authuser.domain.models.UserModel;

import java.util.UUID;

public interface UserCourseService {

    UserCourseDTO save(UUID userId, UserCourseRequestDTO userCourseRequestDTO);

    boolean existsByUserAndCourseId(UserModel user, UUID courseId);

    void delete(UUID courseId);

    boolean existsByCourseId(UUID courseId);
}
