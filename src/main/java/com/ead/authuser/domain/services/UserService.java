package com.ead.authuser.domain.services;

import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll();

    UserDTO findById(UUID userId);

    UserDTO update(UUID userId, UserUpdateRequestDTO userUpdateDTO);

    UserDTO save(UserRequestDTO userRequestDTO);

    UserDTO updateImage(UUID userId, UserUpdateImageRequestDTO updateImageDTO);

    void updatePassword(UUID userId, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO);

    void delete(UUID userId);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);

}
