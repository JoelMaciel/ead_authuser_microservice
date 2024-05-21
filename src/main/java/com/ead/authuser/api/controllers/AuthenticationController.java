package com.ead.authuser.api.controllers;

import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.save(userRequestDTO);
    }
}
