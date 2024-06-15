package com.ead.authuser.api.clients;

import com.ead.authuser.domain.dtos.response.CourseDTO;
import com.ead.authuser.domain.dtos.response.ResponsePageDTO;
import com.ead.authuser.domain.services.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Component
public class CourseClient {

    private final RestTemplate restTemplate;
    private final UtilsService utilsService;

    @Value("${ead.api.url.course}")
    String REQUEST_URL_COURSE;

    // @Retry(name = "retryInstance", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "courseService")
    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable, String token) {
        List<CourseDTO> searchResult = null;
        ResponseEntity<ResponsePageDTO<CourseDTO>> result = null;

        String url = REQUEST_URL_COURSE + utilsService.createUrlGetAllCoursesByUser(userId, pageable);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> requestEntity = new HttpEntity<String>("parameters", headers);

        log.info("Request URL: {} ", url);

        ParameterizedTypeReference<ResponsePageDTO<CourseDTO>> responseType =
                new ParameterizedTypeReference<ResponsePageDTO<CourseDTO>>() {
                };

        result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);

        searchResult = result.getBody().getContent();

        log.info("Ending request /courses userId {} ", userId);
        return result.getBody();
    }
}
