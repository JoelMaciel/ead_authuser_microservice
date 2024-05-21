package com.ead.authuser.api.controllers;

import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    public static final String MSG_UPDATE_PASSWORD = "Password updated successfully.";
    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDTO getOneUser(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequestDTO userUpdate) {
        return userService.update(userId, userUpdate);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable UUID userId, @RequestBody UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        userService.updatePassword(userId, userUpdatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(MSG_UPDATE_PASSWORD);
    }

    @PatchMapping("/{userId}/image")
    public UserDTO updateImage(@PathVariable UUID userId, @RequestBody UserUpdateImageRequestDTO updateImageDTO) {
        return  userService.updateImage(userId, updateImageDTO);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}
