package com.ead.authuser.domain.services.impl;

import com.ead.authuser.api.configs.security.AuthenticationCurrentUserService;
import com.ead.authuser.api.controllers.UserController;
import com.ead.authuser.api.publishers.UserEventPublisher;
import com.ead.authuser.domain.converter.UserConverter;
import com.ead.authuser.domain.dtos.request.*;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.enums.ActionType;
import com.ead.authuser.domain.enums.RoleType;
import com.ead.authuser.domain.enums.UserStatus;
import com.ead.authuser.domain.enums.UserType;
import com.ead.authuser.domain.exceptions.*;
import com.ead.authuser.domain.models.RoleModel;
import com.ead.authuser.domain.models.UserModel;
import com.ead.authuser.domain.repositories.UserRepository;
import com.ead.authuser.domain.services.RoleService;
import com.ead.authuser.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String MSG_USERNAME_ALREADY_EXISTS = "There is already a user registered with this Username.";
    public static final String MSG_EMAIL_ALREADY_EXISTS = "This email is already registered in the database.";
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserEventPublisher userEventPublisher;
    private final RoleService roleService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    @Override
    public Page<UserDTO> findAll(Specification<UserModel> spec, Pageable pageable) {
        UserDetails userDetails = (UserDetails) authenticationCurrentUserService.getAuthentication().getPrincipal();
        log.info("Authentication {} ", userDetails.getUsername());

        Page<UserModel> userModelPage = userRepository.findAll(spec, pageable);
        Page<UserDTO> usersPageDTO = userConverter.toDTOPage(userModelPage);

        addHateoasLinks(usersPageDTO);

        log.debug("GET UserDTO received: {}", usersPageDTO.toString());

        return usersPageDTO;
    }

    @Override
    public UserDTO findById(UUID userId) {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();

        if (currentUserId.equals(userId)) {
            UserModel user = optionalUser(userId);
            log.debug("GET UserDTO received {} ", user.toString());
            return userConverter.toDTO(user);
        } else {
            throw new UserDoesNotHavePermissionException("Forbidden");
        }
    }

    @Transactional
    @Override
    public UserDTO update(UUID userId, UserUpdateRequestDTO userUpdateDTO) {
        UserModel user = optionalUser(userId);
        UserModel userUpdate = userConverter.toUpdateEntity(userUpdateDTO, user);
        userRepository.save(userUpdate);
        userEventPublisher.publishUserEvent(userConverter.toEventDTO(userUpdate, ActionType.UPDATE));
        log.debug("UPDATE User and send broker {} -> ", userUpdate.toString());
        return userConverter.toDTO(userUpdate);
    }

    @Transactional
    @Override
    public UserDTO save(UserRequestDTO userRequestDTO) {
        log.debug("POST registerUser UserRequestDTO received {} ", userRequestDTO.toString());
        UserModel userModel = userConverter.toEntity(userRequestDTO);

        validateUser(userRequestDTO);

        userModel = userConverter.configureUserStatusAndType(userModel, UserStatus.ACTIVE, UserType.STUDENT);

        addRoleToUser(userModel, RoleType.ROLE_STUDENT);

        UserModel userSaved = userRepository.save(userModel);

        userEventPublisher.publishUserEvent(userConverter.toEventDTO(userSaved, ActionType.CREATE));
        log.debug("POST - UserModel saved and send broker ->  {} ", userSaved.toString());

        return userConverter.toDTO(userSaved);
    }

    @Override
    @Transactional
    public UserDTO saveInstructor(InstructorRequestDTO instructorRequestDTO) {
        UserModel user = optionalUser(instructorRequestDTO.getUserId());
        UserModel userInstructor = userConverter.toInstructor(user);

        addInstructorRoleToUser(userInstructor);
        log.info("Adding Role : {}", RoleType.ROLE_INSTRUCTOR);

        UserModel userUpdated = userRepository.save(userInstructor);

        userEventPublisher.publishUserEvent(userConverter.toEventDTO(userUpdated, ActionType.UPDATE));
        log.info("UserInstructorId save and send broker {} ", userUpdated.getUserId());

        return userConverter.toDTO(userUpdated);
    }

    @Override
    @Transactional
    public UserDTO updateImage(UUID userId, UserUpdateImageRequestDTO updateImageDTO) {
        UserModel userModel = optionalUser(userId);
        UserModel newUser = userConverter.toUpdateImageEntity(userModel, updateImageDTO);
        log.info("PATCH updated image {} ", newUser.toString());
        return userConverter.toDTO(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        UserModel user = optionalUser(userId);

        validatePassword(userUpdatePasswordRequestDTO, user);

        UserModel userUpdated = userConverter.toUpdatePasswordEntity(user, userUpdatePasswordRequestDTO);
        log.info("PATCH updated password {} ", userUpdated.toString());
        userRepository.save(userUpdated);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        UserModel userModel = optionalUser(userId);
        userRepository.deleteById(userId);

        userEventPublisher.publishUserEvent(userConverter.toEventDTO(userModel, ActionType.DELETE));
        log.info("UserModel Deleted and send broker {} ", userId);
    }

    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserModel optionalUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserModel addInstructorRoleToUser(UserModel userInstructor) {
        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_INSTRUCTOR);
        return userConverter.addRoleToUser(userInstructor, roleModel);
    }


    private void addRoleToUser(UserModel user, RoleType roleType) {
        RoleModel role = roleService.findByRoleName(roleType);
        user.getRoles().add(role);
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
