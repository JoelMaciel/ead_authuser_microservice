package com.ead.authuser.api.controllers;

import com.ead.authuser.domain.dtos.request.UserRequestDTO;
import com.ead.authuser.utils.TestUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerE2ETest {

    public static final String MSG_INVALID_DATA = "Invalid data";

    @LocalServerPort
    private int port;

    @Autowired
    private Flyway flyway;


    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/api/auth";

        flyway.migrate();
    }

    @Test
    @DisplayName("Given UserRequestDTO Valid When Save User Then Should Return Status 201")
    void givenUserRequestDTOValid_WhenSaveUser_ThenShouldReturnStatus201() {
        UserRequestDTO userRequestDTO = TestUtil.getUserRequestDTOMock();
        String username = "nametest";
        String cpf = "259.725.320-18";

        given()
                .body(userRequestDTO)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
             .when()
                .post("/signup")
             .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", equalTo(username))
                .body("cpf", equalTo(cpf));
    }

    @Test
    @DisplayName("Given Invalid UserRequestDTO  When Save User Then Should Throw Status 400")
    void givenInvalidUserRequestDTO_WhenSaveUser_ThenShouldThrowStatus400() {
        UserRequestDTO userRequestDTO = TestUtil.getInvalidFieldsUserRequestDTOMock();

        String messageExpectedUsername = "Username must be at least 6 characters.";
        String messageExpectedCpfInvalid = "Invalid CPF. Enter a valid CPF.";
        String messageExpectedPhoneNumber = "PhoneNumber is required.";
        String messageExpectedEmail = "Invalid Email. Enter it correctly, example: example@email.com";
        String messageExpectedFullName = "FullName must be at least 10 characters.";

        given()
                .body(userRequestDTO)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
             .when()
                .post("/signup")
             .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("title", equalTo(MSG_INVALID_DATA))
                .body("objects.find { it.name == 'username' }.userMessage", equalTo(messageExpectedUsername))
                .body("objects.find { it.name == 'fullName' }.userMessage", equalTo(messageExpectedFullName))
                .body("objects.find { it.name == 'cpf' }.userMessage", equalTo(messageExpectedCpfInvalid))
                .body("objects.find { it.name == 'phoneNumber' }.userMessage", equalTo(messageExpectedPhoneNumber))
                .body("objects.find { it.name == 'email' }.userMessage", equalTo(messageExpectedEmail));
    }
}