package com.ead.authuser.api.controllers;

import com.ead.authuser.api.clients.CourseClient;
import com.ead.authuser.domain.dtos.UserCourseDTO;
import com.ead.authuser.domain.dtos.request.UserCourseRequestDTO;
import com.ead.authuser.domain.dtos.response.CourseDTO;
import com.ead.authuser.domain.services.UserCourseService;
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
public class UserCourseController {

    public static final String MSG_DELETED_SUCCESSFULLY = "UserCourse deleted successfully";
    private final CourseClient courseClient;
    private final UserCourseService userCourseService;

    @GetMapping("/api/users/{userId}/courses")
    public ResponseEntity<Page<CourseDTO>> getAllCoursesByUser(
            @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(courseClient.getAllCoursesByUser(userId, pageable));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/users/{userId}/courses/subscription")
    public UserCourseDTO saveSubscriptionUserInCourse(
            @PathVariable UUID userId,
            @RequestBody @Valid UserCourseRequestDTO userCourseRequestDTO) {
        return userCourseService.save(userId, userCourseRequestDTO);
    }

    @DeleteMapping("/api/users/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteUserCourseByCourse(@PathVariable UUID courseId) {
        userCourseService.delete(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(MSG_DELETED_SUCCESSFULLY);
    }

}
