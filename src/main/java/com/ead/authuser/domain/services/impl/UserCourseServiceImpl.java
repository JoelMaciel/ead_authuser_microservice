package com.ead.authuser.domain.services.impl;

import com.ead.authuser.domain.converter.UserConverter;
import com.ead.authuser.domain.dtos.UserCourseDTO;
import com.ead.authuser.domain.dtos.request.UserCourseRequestDTO;
import com.ead.authuser.domain.exceptions.SubscriptionAlreadyExistsException;
import com.ead.authuser.domain.models.UserCourseModel;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserCourseRepository;
import com.ead.authuser.domain.services.UserCourseService;
import com.ead.authuser.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserCourseServiceImpl implements UserCourseService {

    public static final String ALREADY_ENROLLED_IN_THIS_COURSE = "User already enrolled in this course";
    private final UserCourseRepository userCourseRepository;
    private final UserService userService;

    @Transactional
    @Override
    public UserCourseDTO save(UUID userId, UserCourseRequestDTO userCourseRequestDTO) {
        UserModel user = userService.optionalUser(userId);

        validateSubscriptionDoesNotExist(user, userCourseRequestDTO.getCourseId());

        UserCourseModel userCourseModel = toEntity(userCourseRequestDTO, user);
        return toDTO(userCourseRepository.save(userCourseModel));
    }

    @Override
    public boolean existsByUserAndCourseId(UserModel user, UUID courseId) {
        return userCourseRepository.existsByUserAndCourseId(user, courseId);
    }

    private void validateSubscriptionDoesNotExist(UserModel user, UUID courseId) {
        if (existsByUserAndCourseId(user, courseId)) {
            throw new SubscriptionAlreadyExistsException(ALREADY_ENROLLED_IN_THIS_COURSE);
        }
    }

    private UserCourseModel toEntity(UserCourseRequestDTO userCourseRequestDTO, UserModel user) {
        return UserCourseModel.builder()
                .user(user)
                .courseId(userCourseRequestDTO.getCourseId())
                .build();
    }

    private UserCourseDTO toDTO(UserCourseModel userCourseModel) {
        return UserCourseDTO.builder()
                .id(userCourseModel.getId())
                .user(UserConverter.toDTO(userCourseModel.getUser()))
                .courseId(userCourseModel.getCourseId())
                .build();
    }
}
