
package com.ead.authuser.api.controllers;

import com.ead.authuser.domain.dtos.request.UserUpdateImageRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.ead.authuser.domain.dtos.request.UserUpdateRequestDTO;
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

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerE2ETest {

    public static final String MSG_USER_NOT_FOUND = "User not found for this userId ";
    public static final String MSG_INVALID_DATA = "Invalid data";

    @LocalServerPort
    private int port;

    @Autowired
    private Flyway flyway;

    private UUID validUserId;
    private UUID invalidUserId;


    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/api/users";

        validUserId = UUID.fromString("5a96aa84-1f15-4333-ba60-54d99a3faccb");
        invalidUserId = UUID.randomUUID();

        flyway.migrate();
    }

    @Test
    @DisplayName("Given Valid Request When GetAllUsers Then Return User Page")
    void givenValidRequest_WhenGetAllUsers_ThenReturnUserPage() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .get()
             .then()
                .body("totalElements", equalTo(3))
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Given Valid UserStatus When GetAllUsers Then Return To Active User ")
    void givenValidUserStatus_WhenGetAllUsers_ThenReturnToActiveUser() {
        given()
                .queryParam("userStatus", "ACTIVE")
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .get()
             .then()
                .body("numberOfElements", equalTo(2))
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Given Valid UserId When GetOneUser Then Return UserDTO")
    void givenValidUserId_WhenGetUserById_ThenReturnUser() {
        given()
                .pathParam("userId", validUserId)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .get("/{userId}", validUserId)
             .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Given Invalid UserId When GetOneUser Then Throw Exception")
    void givenInvalidUserId_WhenGetUserById_ThenThrowException() {
        given()
                .pathParam("userId", invalidUserId)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .get("/{userId}", invalidUserId)
             .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("userMessage", equalTo(MSG_USER_NOT_FOUND + invalidUserId));
    }

    @DisplayName("Given Valid UserUpdateRequestDTO When UpdateUser Then Return UserDTO ")
    @Test
    void givenValidUserUpdateRequestDTO_WhenUpdateUser_ThenReturnUserDTO() {
        UserUpdateRequestDTO userUpdateRequest = TestUtil.getUserUpdateRequestDTO();
        String username = "maciel_update";
        String fullName = "maciel_viana_updated";
        String phoneNumber = "+55 08599998855";

        given()
                .pathParam("userId", validUserId)
                .body(userUpdateRequest)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}")
             .then()
                .statusCode(HttpStatus.SC_OK)
                .body("username", equalTo(username))
                .body("fullName", equalTo(fullName))
                .body("phoneNumber", equalTo(phoneNumber));

    }

    @DisplayName("Given Invalid UserUpdateRequestDTO When UpdateUser Then Throw Status Code 400 ")
    @Test
    void givenInvalidUserUpdateRequestDTO_WhenUpdateUser_ThenThrowStatusCode400() {
        UserUpdateRequestDTO userUpdateRequest = TestUtil.getInvalidUserUpdateRequestDTO();
        String messageInvalidData = "One or more fields are invalid. Fill in correctly and try again.";

        given()
                .pathParam("userId", validUserId)
                .body(userUpdateRequest)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}")
             .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("title", equalTo(MSG_INVALID_DATA))
                .body("userMessage", equalTo(messageInvalidData))
                .body("objects.find { it.name == 'username' }.userMessage", equalTo("username is required"))
                .body("objects.find { it.name == 'fullName' }.userMessage", equalTo("fullName is required"));
    }

    @DisplayName("Given Invalid UserId When UpdateUser Then Throw Status Code 404")
    @Test
    void givenInvalidUserId_WhenUpdateUser_ThenThrowStatusCode404() {
        UserUpdateRequestDTO userUpdateRequest = TestUtil.getUserUpdateRequestDTO();

        given()
                .pathParam("userId", invalidUserId)
                .body(userUpdateRequest)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}")
             .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("userMessage", equalTo(MSG_USER_NOT_FOUND + invalidUserId));
    }

    @DisplayName("Given Valid UserId And Password When UpdatePassword Then Return Status Code 200")
    @Test
    void givenValidUserIdAndPassword_WhenUpdatePassword_ThenReturnStatusCode200() {

        UserUpdatePasswordRequestDTO passwordUpdateRequest = TestUtil.getUserUpdatePasswordRequestDTO();
        String MSG_UPDATE_PASSWORD = "Password updated successfully.";

        given()
                .pathParam("userId", validUserId)
                .body(passwordUpdateRequest)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/password")
             .then()
                .statusCode(HttpStatus.SC_OK)
                .body(equalTo(MSG_UPDATE_PASSWORD));
    }

    @DisplayName("Given Valid UserId And Password When UpdatePassword Then Throw Status Code 404")
    @Test
    void givenInvalidUserI_WhenUpdatePassword_ThenThrowStatusCode404() {
        UserUpdatePasswordRequestDTO passwordUpdateRequest = TestUtil.getUserUpdatePasswordRequestDTO();

        given()
                .pathParam("userId", invalidUserId)
                .body(passwordUpdateRequest)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/password")
             .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("userMessage", equalTo(MSG_USER_NOT_FOUND + invalidUserId));
    }

    @DisplayName("Given Invalid Password When UpdatePassword Then Throw Status Code 400")
    @Test
    void givenInvalidPassword_WhenUpdatePassword_ThenThrowStatusCode400() {
        UserUpdatePasswordRequestDTO invalidPasswordUpdate = TestUtil.getUserUpdateInvalidPasswordRequestDTO();
        String userMessageExpected = "Password must be at least 8 characters.";

        given()
                .pathParam("userId", validUserId)
                .body(invalidPasswordUpdate)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/password")
             .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("title", equalTo(MSG_INVALID_DATA))
                .body("objects.find { it.name == 'password' }.userMessage", equalTo(userMessageExpected));
    }

    @DisplayName("Given Null Fields Password When UpdatePassword Then Throw Status Code 400")
    @Test
    void givenNullFieldsPassword_WhenUpdatePassword_ThenThrowStatusCode400() {
        UserUpdatePasswordRequestDTO invalidPasswordUpdate = TestUtil.getUserUpdateNullFieldsPasswordRequestDTO();
        String userMessagePassword = "password is required";
        String userMessageOldPassword = "oldPassword is required";
        String userMessageDetailExpected = "One or more fields are invalid. Fill in correctly and try again.";

        given()
                .pathParam("userId", validUserId)
                .body(invalidPasswordUpdate)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/password")
             .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("title", equalTo(MSG_INVALID_DATA))
                .body("detail", equalTo(userMessageDetailExpected))
                .body("objects.find { it.name == 'password' }.userMessage", equalTo(userMessagePassword))
                .body("objects.find { it.name == 'oldPassword' }.userMessage", equalTo(userMessageOldPassword));
    }


    @DisplayName("Given Valid UserUpdateImageRequestDTO When UpdateImage Then Return Status Code 200")
    @Test
    void givenNullImage_WhenUpdateImage_ThenReturnStatusCode200() {
        UserUpdateImageRequestDTO updateImageRequestDTO = TestUtil.getUpdateImageRequestDTO();

        given()
                .pathParam("userId", validUserId)
                .body(updateImageRequestDTO)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/image")
             .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @DisplayName("Given Invalid UserId When UpdateImage Then Throw Status Code 404")
    @Test
    void givenInvalidUserId_WhenUpdateImage_ThenThrowStatusCode404() {
        UserUpdateImageRequestDTO updateImageRequestDTO = TestUtil.getUpdateImageRequestDTO();

        given()
                .pathParam("userId", invalidUserId)
                .body(updateImageRequestDTO)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/image")
             .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("userMessage", equalTo(MSG_USER_NOT_FOUND + invalidUserId));
    }

    @DisplayName("Given Blank ImageUrl When UpdateImage Then Throw Status Code 400")
    @Test
    void givenImageUrlNull_WhenUpdateImage_ThenThrowStatusCode400() {
        UserUpdateImageRequestDTO updateBlankImageRequestDTO = TestUtil.getUpdateBlankImageRequestDTO();
        String messageExpected = "imageUrl is required";

        given()
                .pathParam("userId", validUserId)
                .body(updateBlankImageRequestDTO)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .patch("/{userId}/image")
             .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("objects.find { it.name == 'imageUrl' }.userMessage", equalTo(messageExpected));
    }


    @DisplayName("Given Valid User ID When Delete User Then Return Status Code 204")
    @Test
    void givenValidUserId_WhenDeleteUser_ThenReturnStatusCode204() {

        given()
                .pathParam("userId", validUserId)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .delete("/{userId}")
             .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @DisplayName("Given Invalid User ID When Delete User Then Throw Status Code 404")
    @Test
    void givenValidUserId_WhenDeleteUser_ThenThrowStatusCode404() {

        given()
                .pathParam("userId", invalidUserId)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
             .when()
                .delete("/{userId}")
             .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("userMessage", equalTo(MSG_USER_NOT_FOUND + invalidUserId));
    }

}
