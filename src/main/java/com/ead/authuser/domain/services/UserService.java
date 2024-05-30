package com.ead.authuser.domain.services;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface UserService {

    Page<UserDTO> findAll(Specification<UserModel> spec, Pageable pageable, UUID courseId);

    UserDTO findById(UUID userId);

    UserDTO update(UUID userId, UserUpdateRequestDTO userUpdateDTO);

    UserDTO save(UserRequestDTO userRequestDTO);

    UserDTO updateImage(UUID userId, UserUpdateImageRequestDTO updateImageDTO);

    void updatePassword(UUID userId, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO);

    void delete(UUID userId);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);

}
