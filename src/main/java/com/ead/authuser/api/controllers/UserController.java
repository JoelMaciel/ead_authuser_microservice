package com.ead.authuser.api.controllers;

import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.services.UserService;
import com.ead.authuser.domain.specification.SpecificationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    public static final String MSG_UPDATE_PASSWORD = "Password updated successfully.";
    private final UserService userService;

    @GetMapping
    public Page<UserDTO> getAllUsers(
            SpecificationTemplate.UserSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userService.findAll(spec, pageable);
    }

    @GetMapping("/{userId}")
    public UserDTO getOneUser(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@PathVariable UUID userId, @RequestBody @Valid UserUpdateRequestDTO userUpdate) {
        return userService.update(userId, userUpdate);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(
            @PathVariable UUID userId,
            @RequestBody @Valid UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO
    ) {
        userService.updatePassword(userId, userUpdatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(MSG_UPDATE_PASSWORD);
    }

    @PatchMapping("/{userId}/image")
    public UserDTO updateImage(@PathVariable UUID userId, @RequestBody @Valid UserUpdateImageRequestDTO updateImageDTO) {
        return userService.updateImage(userId, updateImageDTO);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}
