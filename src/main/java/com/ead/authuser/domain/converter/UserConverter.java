package com.ead.authuser.domain.converter;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.models.UserModel;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;

public class UserConverter {

    private UserConverter() {
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
        return UserModel.builder()
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .fullName(userRequestDTO.getFullName())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .cpf(userRequestDTO.getCpf())
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
}
