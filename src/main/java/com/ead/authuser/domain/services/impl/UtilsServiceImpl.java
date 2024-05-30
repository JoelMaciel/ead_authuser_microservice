package com.ead.authuser.domain.services.impl;

import com.ead.authuser.domain.services.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

    String REQUEST_URI = "http://localhost:8082";

    @Override
    public String createUrlGetAllCoursesByUser(UUID userId, Pageable pageable) {
        return "/api/courses?userId=" + userId + "&page=" + pageable.getPageNumber()
                + "&size=" + pageable.getPageSize() + "&sort=" + pageable.getSort().toString()
                .replaceAll(": ", ",");
    }
}
