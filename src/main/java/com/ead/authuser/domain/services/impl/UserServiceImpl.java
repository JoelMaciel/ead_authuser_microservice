package com.ead.authuser.domain.services.impl;

import com.ead.authuser.api.controllers.UserController;
import com.ead.authuser.domain.converter.UserConverter;
import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.enums.UserStatus;
import com.ead.authuser.domain.enums.UserType;
import com.ead.authuser.domain.exceptions.EmailAlreadyExistsException;
import com.ead.authuser.domain.exceptions.PasswordMismatchedException;
import com.ead.authuser.domain.exceptions.UserNotFoundException;
import com.ead.authuser.domain.exceptions.UsernameAlreadyExistsException;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserRepository;
import com.ead.authuser.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String MSG_USERNAME_ALREADY_EXISTS = "There is already a user registered with this Username.";
    public static final String MSG_EMAIL_ALREADY_EXISTS = "This email is already registered in the database.";
    private final UserRepository userRepository;

    @Override
    public Page<UserDTO> findAll(Specification<UserModel> spec, Pageable pageable) {
        Page<UserModel> users = userRepository.findAll(spec, pageable);
        Page<UserDTO> usersPageDTO = UserConverter.toDTOPage(users);
        addHateoasLinks(usersPageDTO);
        log.debug("GET  UserDTO received {} ", usersPageDTO.toString());

        return usersPageDTO;
    }

    @Override
    public UserDTO findById(UUID userId) {
        UserModel user = optionalUser(userId);
        log.debug("GET UserDTO received {} ", user.toString());
        return UserConverter.toDTO(user);
    }

    @Transactional
    @Override
    public UserDTO update(UUID userId, UserUpdateRequestDTO userUpdateDTO) {
        UserModel user = optionalUser(userId);
        UserModel userUpdate = UserConverter.toUpdateEntity(userUpdateDTO, user);
        log.debug("PUT  UserDTO updated received {} ", userUpdate.toString());
        return UserConverter.toDTO(userRepository.save(userUpdate));
    }

    @Transactional
    @Override
    public UserDTO save(UserRequestDTO userRequestDTO) {
        log.debug("POST registerUser UserRequestDTO received {} ", userRequestDTO.toString());
        UserModel userModel = UserConverter.toEntity(userRequestDTO);

        validateUser(userRequestDTO);

        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);

        UserModel userSaved = userRepository.save(userModel);
        log.debug("POST registerUser UserModel saved {} ", userSaved.toString());
        return UserConverter.toDTO(userSaved);
    }

    @Override
    @Transactional
    public UserDTO updateImage(UUID userId, UserUpdateImageRequestDTO updateImageDTO) {
        UserModel userModel = optionalUser(userId);
        UserModel newUser = UserConverter.toUpdateImageEntity(userModel, updateImageDTO);
        log.debug("PATCH updated image {} ", newUser.toString());
        return UserConverter.toDTO(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        UserModel user = optionalUser(userId);

        validatePassword(userUpdatePasswordRequestDTO, user);

        UserModel userUpdated = UserConverter.toUpdatePasswordEntity(user, userUpdatePasswordRequestDTO);
        log.debug("PATCH updated password {} ", userUpdated.toString());
        userRepository.save(userUpdated);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        optionalUser(userId);
        log.debug("DELETE UserModel Deleted {} ", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserModel optionalUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void addHateoasLinks(Page<UserDTO> usersDtos) {
        if (!usersDtos.isEmpty()) {
            for (UserDTO userDTO : usersDtos) {
                userDTO.add(linkTo(methodOn(UserController.class).getOneUser(userDTO.getUserId())).withSelfRel());
            }
        }
    }

    private void validatePassword(UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO, UserModel user) {
        if (!user.getPassword().equals(userUpdatePasswordRequestDTO.getOldPassword())) {
            throw new PasswordMismatchedException("Mismatched old password");
        }
    }

    private void validateUser(UserRequestDTO userRequestDTO) {
        if (existsByUserName(userRequestDTO.getUsername())) {
            throw new UsernameAlreadyExistsException(MSG_USERNAME_ALREADY_EXISTS);
        }

        if (existsByEmail(userRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(MSG_EMAIL_ALREADY_EXISTS);
        }
    }
}
