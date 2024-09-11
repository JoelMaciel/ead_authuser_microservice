package com.ead.authuser.domain.services.impl;

import com.ead.authuser.api.configs.security.AuthenticationCurrentUserService;
import com.ead.authuser.api.configs.security.UserDetailsImpl;
import com.ead.authuser.api.publishers.UserEventPublisher;
import com.ead.authuser.domain.converter.UserConverter;
import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.enums.RoleType;
import com.ead.authuser.domain.enums.UserStatus;
import com.ead.authuser.domain.enums.UserType;
import com.ead.authuser.domain.exceptions.UserNotFoundException;
import com.ead.authuser.domain.models.RoleModel;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserRepository;
import com.ead.authuser.domain.services.RoleService;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserConverter userConverter;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventPublisher userEventPublisher;

    @Mock
    private AuthenticationCurrentUserService authenticationCurrentUserService;

    @Captor
    private ArgumentCaptor<UserModel> userModelCaptor;

    private UserModel userModel;
    private UserRequestDTO userRequestDTO;
    private UserUpdateRequestDTO userUpdateRequestDTO;
    private UUID invalidUserId;
    private RoleModel roleModel;


    @BeforeEach
    void setUp() {
        openMocks(this);

        initializeTestObjects();
        configureSecurityContext();
        roleModel = new RoleModel(UUID.randomUUID(), RoleType.ROLE_STUDENT);

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
        when(authenticationCurrentUserService.getCurrentUser()).thenReturn(UserDetailsImpl.build(userModel));
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
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findById(invalidUserId));

        assertEquals("User not found for this userId " + invalidUserId, exception.getMessage());

        verify(userRepository, times(1)).findById(invalidUserId);
    }

    @Test
    @DisplayName("Given UserRequestDTO Valid When Save User Then Should Save User Successfully")
    void givenUserValid_WhenSaveUser_ThenShouldSaveUserSuccessfully() {
        when(userConverter.toEntity(any(UserRequestDTO.class))).thenReturn(userModel);
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleService.findByRoleName(RoleType.ROLE_STUDENT)).thenReturn(roleModel);

        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setUserId(userModel.getUserId());
        expectedUserDTO.setUsername(userModel.getUsername());
        expectedUserDTO.setEmail(userModel.getEmail());
        expectedUserDTO.setUserStatus(UserStatus.ACTIVE.name());
        expectedUserDTO.setUserType(UserType.STUDENT.name());

        when(userConverter.toDTO(any(UserModel.class))).thenReturn(expectedUserDTO);
        when(userRepository.existsByUsername(userRequestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);

        UserDTO userDTO = userService.save(userRequestDTO);

        assertNotNull(userDTO);
        assertEquals(userRequestDTO.getUsername(), userDTO.getUsername());
        assertEquals(userRequestDTO.getEmail(), userDTO.getEmail());
        assertEquals(UserStatus.ACTIVE.name(), userDTO.getUserStatus());
        assertEquals(UserType.STUDENT.name(), userDTO.getUserType());

        verify(userRepository, times(1)).save(userModel);
        assertTrue(userModel.getRoles().contains(roleModel));
        verify(userEventPublisher, times(1)).publishUserEvent(any());
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
                () -> userService.update(invalidUserId, userUpdateRequestDTO));

        assertEquals("User not found for this userId " + invalidUserId, exception.getMessage());

        verify(userRepository, times(1)).findById(invalidUserId);
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

    private void configureSecurityContext() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(userModel);
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userDetails, null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void initializeTestObjects() {
        userModel = TestUtil.getUserModelMock();
        userRequestDTO = TestUtil.getUserRequestDTOMock();
        userUpdateRequestDTO = TestUtil.getUserUpdateRequestDTO();
        invalidUserId = UUID.randomUUID();

        RoleModel studentRole = new RoleModel(UUID.randomUUID(), RoleType.ROLE_STUDENT);
        userModel.getRoles().add(studentRole);
    }

}