package com.ead.authuser.domain.services.impl;

import com.ead.authuser.domain.repositories.UserCourseRepository;
import com.ead.authuser.domain.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCourseServiceImpl implements UserCourseService {

    private final UserCourseRepository userCourseRepository;
}
