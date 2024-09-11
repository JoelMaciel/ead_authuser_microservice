package com.ead.authuser.utils;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.enums.UserStatus;
import com.ead.authuser.domain.enums.UserType;
import com.ead.authuser.domain.models.UserModel;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.UUID;

public class TestUtil {

    public static UserModel getUserModelMock() {
        UserModel user = UserModel.builder()
                .userId(UUID.randomUUID())
                .username("joelmacieltest")
                .email("joelteste@example.com")
                .password("123456")
                .fullName("user1joel")
                .phoneNumber("+55 085999884477")
                .userStatus(UserStatus.ACTIVE)
                .userType(UserType.STUDENT)
                .cpf("908599956565")
                .roles(new HashSet<>())
                .creationDate(OffsetDateTime.now())
                .updateDate(OffsetDateTime.now())
                .build();

        return user;
    }

    public static UserRequestDTO getUserRequestDTOMock() {
        return UserRequestDTO.builder()
                .username("nametest")
                .email("joelll@gmail.com")
                .fullName("fullnametest")
                .phoneNumber("+55 085999999999")
                .password("12345678")
                .cpf("259.725.320-18")
                .build();
    }

    public static UserRequestDTO getInvalidFieldsUserRequestDTOMock() {
        return UserRequestDTO.builder()
                .username("joel")
                .email("joelgmail.com")
                .fullName("maciel")
                .phoneNumber("")
                .password("12345678")
                .cpf("0000212")
                .build();
    }

    public static UserRequestDTO getInvalidUserRequestDTOMock() {
        return UserRequestDTO.builder()
                .username("nametest")
                .email("joelll@gmail.com")
                .fullName("fullnametest")
                .build();
    }


    public static UserUpdateRequestDTO getUserUpdateRequestDTO() {
        return UserUpdateRequestDTO.builder()
                .username("maciel_update")
                .fullName("maciel_viana_updated")
                .phoneNumber("+55 08599998855")
                .build();
    }

    public static UserUpdateRequestDTO getInvalidUserUpdateRequestDTO() {
        return UserUpdateRequestDTO.builder()
                .username(null)
                .fullName(null)
                .phoneNumber("")
                .build();
    }

    public static UserUpdatePasswordRequestDTO getUserUpdatePasswordRequestDTO() {
        return UserUpdatePasswordRequestDTO.builder()
                .password("2233445566")
                .oldPassword("123456")
                .build();
    }

    public static UserUpdatePasswordRequestDTO getUserUpdateInvalidPasswordRequestDTO() {
        return UserUpdatePasswordRequestDTO.builder()
                .password("123456")
                .oldPassword("123456")
                .build();
    }

    public static UserUpdatePasswordRequestDTO getUserUpdateNullFieldsPasswordRequestDTO() {
        return UserUpdatePasswordRequestDTO.builder()
                .build();
    }

    public static UserUpdateImageRequestDTO getUpdateImageRequestDTO() {
        return UserUpdateImageRequestDTO.builder()
                .imageUrl("http://image_update.jpg")
                .build();
    }

    public static UserUpdateImageRequestDTO getUpdateBlankImageRequestDTO() {
        return UserUpdateImageRequestDTO.builder()
                .imageUrl("")
                .build();
    }
}
