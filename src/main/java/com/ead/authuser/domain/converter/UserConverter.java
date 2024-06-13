package com.ead.authuser.domain.converter;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.dtos.response.UserEventDTO;
import com.ead.authuser.domain.enums.ActionType;
import com.ead.authuser.domain.enums.UserStatus;
import com.ead.authuser.domain.enums.UserType;
import com.ead.authuser.domain.models.UserModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;

@Component
public class UserConverter implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static Page<UserDTO> toDTOPage(Page<UserModel> users) {
        return users.map(UserConverter::toDTO);
    }

    public static UserDTO toDTO(UserModel user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userStatus(user.getUserStatus().toString())
                .userType(user.getUserType().toString())
                .cpf(user.getCpf())
                .phoneNumber(user.getPhoneNumber())
                .imageUrl(user.getImageUrl())
                .creationDate(user.getCreationDate())
                .updateDate((user.getUpdateDate()))
                .build();
    }

    public static UserModel toEntity(UserRequestDTO userRequestDTO) {
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);

        return UserModel.builder()
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .password(encoder.encode(userRequestDTO.getPassword()))
                .fullName(userRequestDTO.getFullName())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .cpf(userRequestDTO.getCpf())
                .roles(new HashSet<>())
                .build();
    }

    public static UserModel toUpdateEntity(UserUpdateRequestDTO userUpdate, UserModel user) {
        return user.toBuilder()
                .username(userUpdate.getUsername())
                .fullName(userUpdate.getFullName())
                .phoneNumber(userUpdate.getPhoneNumber())
                .updateDate(OffsetDateTime.now())
                .build();
    }

    public static UserModel toUpdatePasswordEntity(
            UserModel user,
            UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO
    ) {
        return user.toBuilder()
                .password(userUpdatePasswordRequestDTO.getPassword())
                .updateDate(OffsetDateTime.now())
                .build();
    }

    public static UserModel toUpdateImageEntity(
            UserModel user,
            UserUpdateImageRequestDTO userUpdateImageRequestDTO
    ) {
        return user.toBuilder()
                .imageUrl(userUpdateImageRequestDTO.getImageUrl())
                .updateDate(OffsetDateTime.now())
                .build();
    }

    public static UserModel toInstructor(UserModel user) {
        return user.toBuilder()
                .userType(UserType.INSTRUCTOR)
                .updateDate(OffsetDateTime.now())
                .build();
    }

    public static UserModel configureUserStatusAndType(UserModel user, UserStatus status, UserType type) {
        return user.toBuilder()
                .userStatus(status)
                .userType(type)
                .build();
    }

    public static UserEventDTO toEventDTO(UserModel userModel, ActionType actionType) {
        return UserEventDTO.builder()
                .userId(userModel.getUserId())
                .username(userModel.getUsername())
                .fullName(userModel.getFullName())
                .email(userModel.getEmail())
                .userStatus(String.valueOf(userModel.getUserStatus()))
                .userType(String.valueOf(userModel.getUserType()))
                .cpf(userModel.getCpf())
                .actionType(String.valueOf(actionType))
                .build();
    }

}
