package com.appsdeveloperblog.UsersService.ui;

import com.appsdeveloperblog.UsersService.ui.model.User;
import com.appsdeveloperblog.UsersService.ui.model.UserRest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
public class UsersControllerWithTestContainerITest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:9.2.0");

    @LocalServerPort
    private int port;

    private final String TEST_EMAIL = "test@test.com";
    private final String TEST_PASSWORD = "123456789";
    private String userId;
    private String token;

    private final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter();
    private final ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter();

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port=port;
        RestAssured.filters(requestLoggingFilter, responseLoggingFilter);

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Order(1)
    @Test
    void testContainerIsRunning() {
        assertTrue(mysqlContainer.isRunning());
    }

    @Order(2)
    @Test
    void testCreateUser_whenValidDetailsProvided_returnsCreatedUser() {

        // Arrange
        User newUser = new User("Sergey","Kargopolov",TEST_EMAIL,TEST_PASSWORD);

        // Act
       given()
                .body(newUser)
        .when()
                .post("/users")
        .then()
               .statusCode(201)
               .body("id", notNullValue())
               .body("firstName",equalTo(newUser.getFirstName()))
               .body("lastName",equalTo(newUser.getLastName()))
               .body("email",equalTo(newUser.getEmail()));

    }

    @Test
    @Order(3)
    void testLogin_whenValidCredentialsProvided_returnsTokenAndUserIdHeaders() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", TEST_EMAIL);
        credentials.put("password", TEST_PASSWORD);

        // Act
        Response response = given()
                .body(credentials)
        .when()
                .post("/login");

        this.userId = response.header("userId");
        this.token = response.header("token");

        // Assert
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertNotNull(userId);
        assertNotNull(token);
    }

    @Test
    @Order(4)
    void testGetUser_withValidAuthenticationToken_returnsUser() {
        given() // Arrange
                .pathParam("userId",this.userId)
                .header("Authorization", "Bearer " + this.token)
                //.auth().oauth2(this.token)
        .when() // Act
                .get("/users/{userId}")
        .then() // Assert
                .statusCode(HttpStatus.OK.value())
                .body("id",equalTo(this.userId))
                .body("email",equalTo(TEST_EMAIL))
                .body("firstName", notNullValue())
                .body("lastName", notNullValue());
    }

    @Test
    @Order(5)
    void testGetUser_withMissingAuthHeader_returnsForbidden() {
        given()
                .pathParam("userId", this.userId)
        .when()
                .get("/users/{userId}")
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }


    @Test
    @Order(6)
    void testGetUsers_withValidTokenAndQueryParams_returnsPaginatedUsersList() {
        given()
                .header("Authorization", "Bearer " + this.token)
                .queryParam("page", 1)
                .queryParam("limit", 10)
        .when()
                .get("/users")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1));
    }

}
