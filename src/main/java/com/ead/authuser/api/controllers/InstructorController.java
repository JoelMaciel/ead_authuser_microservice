package com.ead.authuser.api.controllers;

import com.ead.authuser.domain.dtos.request.InstructorRequestDTO;
import com.ead.authuser.domain.dtos.response.UserDTO;
import com.ead.authuser.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subscription")
    public UserDTO saveSubscriptionInstructor(@RequestBody @Valid InstructorRequestDTO instructorRequestDTO) {
        return userService.saveInstructor(instructorRequestDTO);
    }
}
