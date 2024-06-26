package com.ead.authuser.api.controllers;

import com.ead.authuser.api.clients.CourseClient;
import com.ead.authuser.domain.dtos.response.CourseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class UserCourseController {

    private final CourseClient courseClient;

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/api/users/{userId}/courses")
    public ResponseEntity<Page<CourseDTO>> getAllCoursesByUser(
            @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestHeader("Authorization") String token,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(courseClient.getAllCoursesByUser(userId, pageable, token));
    }
}
