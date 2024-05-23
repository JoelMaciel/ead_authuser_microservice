package com.ead.authuser.domain.services.impl;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.exceptions.UserNotFoundException;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserRepository;
import com.ead.authuser.domain.specification.SpecificationTemplate;
import com.ead.authuser.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceImplTest {

    public static final String MSG_USER_NOT_FOUND = "User not found for this userId d935ef3e-b7cf-4efb-a8ca-140ac13ae479";

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<UserModel> userModelCaptor;

    private UserModel userModel;
    private UserRequestDTO userRequestDTO;
    private UserUpdateRequestDTO userUpdateRequestDTO;


    @BeforeEach
    void setUp() {
        openMocks(this);

        userModel = TestUtil.getUserModelMock();
        userRequestDTO = TestUtil.getUserRequestDTOMock();
        userUpdateRequestDTO = TestUtil.getUserUpdateRequestDTO();
    }

    @Test
    @DisplayName("Given UserSpec and Pageable, When findAll is called, Then Should Return Page of UserDTOs")
    void givenUserSpecAndPageable_WhenFindAll_ThenReturnPageOfUserDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        SpecificationTemplate.UserSpec userSpec = mock(SpecificationTemplate.UserSpec.class);
        Page<UserModel> userPage = new PageImpl<>(List.of(userModel), pageable, 1);

        when(userRepository.findAll(any(SpecificationTemplate.UserSpec.class), eq(pageable))).thenReturn(userPage);

        Page<UserDTO> result = userService.findAll(userSpec, pageable);

        assertNotNull(result);
        verify(userRepository).findAll(any(SpecificationTemplate.UserSpec.class), eq(pageable));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Given Valid UserId When FindById Then Must Return User Successfully")
    void givenValidUserId_WhenFindById_ThenMustReturnUserSuccessfully() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userModel));
        UserDTO userDTO = userService.findById(userModel.getUserId());

        assertNotNull(userDTO);

        String nameExpected = "joelmacieltest";
        assertEquals(nameExpected, userDTO.getUsername());

        verify(userRepository, times(1)).findById(userModel.getUserId());
    }

    @Test
    @DisplayName("Given UserId Invalid When FindById Then Should Throw Exception")
    void givenUserIdInvalid_WhenFindById_ThenShouldThrowException() {
        when(userRepository.findById(userModel.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findById(userModel.getUserId()));

        assertEquals(MSG_USER_NOT_FOUND, exception.getMessage());

        verify(userRepository, times(1)).findById(userModel.getUserId());
    }

    @Test
    @DisplayName("Given UserRequestDTO Valid When Save User Then Should Save User Successfully")
    void givenUserValid_WhenSaveUser_ThenShouldSaveUserSuccessfully() {
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO userDTO = userService.save(userRequestDTO);

        assertNotNull(userDTO);
        assertEquals(userRequestDTO.getUsername(), userDTO.getUsername());
        assertEquals(userRequestDTO.getEmail(), userDTO.getEmail());

        String userStatusExpected = "ACTIVE";
        String userTypeExpected = "STUDENT";

        assertEquals(userStatusExpected, userDTO.getUserStatus());
        assertEquals(userTypeExpected, userDTO.getUserType());

        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Valid UserUpdateRequestDTO When Update User Then Should Update User Name Successfully")
    void givenValidUserUpdate_WhenUpdateUser_ThenShouldUpdateNameSuccessfully() {
        UserUpdateRequestDTO userUpdateDTO = TestUtil.getUserUpdateRequestDTO();

        when(userRepository.findById(userModel.getUserId())).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO newUserDTO = userService.update(userModel.getUserId(), userUpdateDTO);

        assertNotNull(newUserDTO);
        assertEquals(userUpdateRequestDTO.getFullName(), newUserDTO.getFullName());
        assertEquals(userUpdateRequestDTO.getUsername(), newUserDTO.getUsername());
        assertEquals(userUpdateRequestDTO.getPhoneNumber(), newUserDTO.getPhoneNumber());

        verify(userRepository, times(1)).findById(userModel.getUserId());
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Invalid UserId When Update UserModel Then Throw Exception")
    void givenUserIdNotFound_WhenUpdatePassword_ThenThrowException() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.update(userModel.getUserId(), userUpdateRequestDTO));

        assertEquals(MSG_USER_NOT_FOUND, exception.getMessage());

        verify(userRepository, times(1)).findById(userModel.getUserId());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Old Password Valid When Update Password Then Should Update Password Successfully")
    void givenOldPasswordValid_WhenUpdatePassword_ThenShouldUpdatePasswordSuccessfully() {
        UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO = TestUtil.getUserUpdatePasswordRequestDTO();

        when(userRepository.findById(userModel.getUserId())).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updatePassword(userModel.getUserId(), userUpdatePasswordRequestDTO);

        verify(userRepository, times(1)).findById(userModel.getUserId());
        verify(userRepository, times(1)).save(userModelCaptor.capture());

        UserModel savedUser = userModelCaptor.getValue();
        String newPassword = "2233445566";

        assertEquals(newPassword, savedUser.getPassword());
    }

    @Test
    @DisplayName("Given New Image When Update Image Then Should Update Image Successfully")
    void givenNewImage_WhenUpdateImageThenShouldUpdateImageSuccessfully() {
        UserUpdateImageRequestDTO userUpdateImageRequestDTO = TestUtil.getUpdateImageRequestDTO();

        when(userRepository.findById(userModel.getUserId())).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO newUserDTO = userService.updateImage(userModel.getUserId(), userUpdateImageRequestDTO);

        assertNotNull(newUserDTO);

        verify(userRepository, times(1)).findById(userModel.getUserId());
        verify(userRepository, times(1)).save(userModelCaptor.capture());

        UserModel savedUser = userModelCaptor.getValue();
        assertEquals(userUpdateImageRequestDTO.getImageUrl(), savedUser.getImageUrl());
    }

    @Test
    @DisplayName("Given Nonexistent User When Update Image Then Should Throw UserNotFoundException")
    void givenNonexistentUser_WhenUpdateImageThenShouldThrowUserNotFoundException() {
        UserUpdateImageRequestDTO userUpdateImageRequestDTO = TestUtil.getUpdateImageRequestDTO();
        UUID invalidUserId = UUID.fromString("12345678-1234-1234-1234-1234567890");


        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {

            userService.updateImage(invalidUserId, userUpdateImageRequestDTO);
        });

        String messageExpected = "User not found for this userId 12345678-1234-1234-1234-001234567890";

        assertEquals(messageExpected, exception.getMessage());

        verify(userRepository, times(1)).findById(invalidUserId);
        verify(userRepository, never()).save(any(UserModel.class));

    }

    @Test
    @DisplayName("Given Existing User When Delete Then Should Delete User Successfully")
    void givenExistingUser_WhenDelete_ThenShouldDeleteUserSuccessfully() {
        when(userRepository.findById(userModel.getUserId())).thenReturn(Optional.of(userModel));

        userService.delete(userModel.getUserId());

        verify(userRepository, times(1)).findById(userModel.getUserId());
        verify(userRepository, times(1)).deleteById(userModel.getUserId());
    }

    @Test
    @DisplayName("Given Nonexistent User When Delete Then Should Throw UserNotFoundException")
    void givenNonexistentUser_WhenDelete_ThenShouldThrowUserNotFoundException() {
        UUID invalidUserId = UUID.fromString("12345678-1234-1234-1234-1234567890");

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.delete(invalidUserId);
        });

        String messageExpected = "User not found for this userId " + invalidUserId;
        assertEquals(messageExpected, exception.getMessage());

        verify(userRepository, times(1)).findById(invalidUserId);
        verify(userRepository, never()).deleteById(userModel.getUserId());
    }
}