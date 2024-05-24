package com.ead.authuser.domain.services.impl;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.exceptions.UserNotFoundException;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserRepository;
import com.ead.authuser.utils.TestUtil;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplIT {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Flyway flyway;

    private UserModel userModel;

    private UUID userId;
    private UUID invalidUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("5a96aa84-1f15-4333-ba60-54d99a3faccb");
        invalidUserId = UUID.randomUUID();

        flyway.migrate();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Given UserSpec and Pageable, When findAll is called, Then Should Return Page of UserDTOs")
    void givenUserSpecAndPageable_WhenFindAll_ThenReturnPageOfUserDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> usersPage = userService.findAll(null, pageable);

        assertFalse(usersPage.isEmpty());
        assertNotNull(usersPage);

        assertThat(userRepository.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("Given UserRequestDTO Valid When Save User Then Should Save User Successfully")
    void givenUserValid_WhenSaveUser_ThenShouldSaveUserSuccessfully() {
        UserRequestDTO userRequestDTO = TestUtil.getUserRequestDTOMock();
        UserDTO userDTO = userService.save(userRequestDTO);

        assertNotNull(userDTO);

        String userStatusExpected = "ACTIVE";
        String userTypeExpected = "STUDENT";

        assertEquals(userStatusExpected, userDTO.getUserStatus());
        assertEquals(userTypeExpected, userDTO.getUserType());
    }

    @Test
    @DisplayName("Given Invalid UserRequestDTO When Save User Then Should Throw Exception")
    void givenUserInvalid_WhenSaveUser_ThenShouldThrowException() {
        UserRequestDTO invalidUserRequestDTO = TestUtil.getInvalidUserRequestDTOMock();

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.save(invalidUserRequestDTO));
    }

    @Test
    @DisplayName("Given Valid UserId When FindById Then Should Return User Successfully")
    void givenValidUserId_WhenFindById_ThenMustReturnUserSuccessfully() {
        UserDTO userDTO = userService.findById(userId);

        assertNotNull(userDTO);

        String username = "John";
        assertEquals(username, userDTO.getUsername());
    }

    @Test
    @DisplayName("Given UserId Invalid When FindById Then Should Throw Exception")
    void givenUserIdInvalid_WhenFindById_ThenShouldThrowException() {


        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findById(invalidUserId));

        String expectedMessage = "User not found for this userId " + invalidUserId;

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Given Valid UserUpdateRequestDTO When Update User Then Should Update User Name Successfully")
    void givenValidUserUpdate_WhenUpdateUser_ThenShouldUpdateNameSuccessfully() {

        UserUpdateRequestDTO userUpdateRequestDTO = TestUtil.getUserUpdateRequestDTO();
        UserDTO newUserDTO = userService.update(userId, userUpdateRequestDTO);

        assertNotNull(newUserDTO);
        assertEquals(userUpdateRequestDTO.getUsername(), newUserDTO.getUsername());
    }


    @Test
    @DisplayName("Given Invalid UserId When Update UserModel Then Throw Exception")
    void givenInvalidUserId_WhenUpdateUserModel_ThenThrowException() {
        UserUpdateRequestDTO userUpdateRequestDTO = TestUtil.getUserUpdateRequestDTO();

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.update(invalidUserId, userUpdateRequestDTO));
        String expectedMessage = "User not found for this userId " + invalidUserId;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Given Invalid UserUpdateRequestDTO When Update UserModel Then Throw Exception")
    void givenUsInvalidUserUpdateRequestDTO_WhenUpdateUserModel_ThenThrowException() {
        UserUpdateRequestDTO userUpdateRequestDTO = TestUtil.getInvalidUserUpdateRequestDTO();

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.update(userId, userUpdateRequestDTO));
    }

    @Test
    @DisplayName("Given Old Password Invalid When Update Password Then Should Update Password Successfully")
    void givenOldPasswordValid_WhenUpdatePassword_ThenShouldUpdatePasswordSuccessfully() {
        UserUpdatePasswordRequestDTO userUpdatePassword = TestUtil.getUserUpdatePasswordRequestDTO();

        userService.updatePassword(userId, userUpdatePassword);

        UserModel user = userService.optionalUser(userId);

        assertEquals(user.getPassword(), userUpdatePassword.getPassword());
    }

    @Test
    @DisplayName("Given New Image When Update Image Then Should Update Image Successfully")
    void givenNewImage_WhenUpdateImageThenShouldUpdateImageSuccessfully() {

        UserUpdateImageRequestDTO updateImageRequestDTO = TestUtil.getUpdateImageRequestDTO();

        UserDTO userDTO = userService.updateImage(userId, updateImageRequestDTO);

        assertNotNull(userDTO);
        assertEquals(updateImageRequestDTO.getImageUrl(), userDTO.getImageUrl());

    }

    @Test
    @DisplayName("Given Nonexistent User When Update Image Then Should Throw UserNotFoundException")
    void givenNonexistentUser_WhenUpdateImageThenShouldThrowUserNotFoundException() {
        UserUpdateImageRequestDTO updateImageRequestDTO = TestUtil.getUpdateImageRequestDTO();

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateImage(invalidUserId, updateImageRequestDTO));

        String expectedMessage = "User not found for this userId " + invalidUserId;
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Given Valid UserId When Delete Then Should Delete User Successfully")
    void givenValidUserId_WhenDelete_ThenShouldDeleteUserSuccessfully() {
        assertDoesNotThrow(() -> userService.delete(userId));

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Given Invalid UserId When Delete Then Should Throw UserNotFoundException")
    void givenInvlidUserId_WhenDelete_ThenShouldThrowUserNotFoundException() {

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.delete(invalidUserId));

        String expectedMessage = "User not found for this userId " + invalidUserId;
        assertEquals(expectedMessage, exception.getMessage());
    }
}
