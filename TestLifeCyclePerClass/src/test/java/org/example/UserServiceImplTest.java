package org.example;

import org.example.io.UsersDatabase;
import org.example.io.UsersDatabaseMapImpl;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceImplTest {
    UsersDatabase usersDatabase;
    UserService userService;

    String createdUserID;

    @BeforeAll
    void setup() {
        usersDatabase = new UsersDatabaseMapImpl();
        usersDatabase.init();
        userService = new UserServiceImpl(usersDatabase);
        // Create & initialize database
    }

    @AfterAll
    void cleanup() {
        usersDatabase.close();
        // Close connection
        // Delete database
    }

    @Test
    @Order(1)
    @DisplayName("Create User works")
    void testCreateUser_whenProvidedWithValidDetails_returnsUserId() {
        //Arrange
        Map<String, String> user = new HashMap<>();
        user.put("firstName", "Sergey");
        user.put("lastName", "Kargopoloy");

        //Act
        createdUserID = userService.createUser(user);

        //Asserts
        assertNotNull(createdUserID, "User id is not null");
    }


    @Test
    @Order(2)
    @DisplayName("Update user works")
    void testUpdateUser_whenProvidedWithValidDetails_returnsUpdatedUserDetails() {
        //Arrange
        Map<String, String> user = new HashMap<>();
        user.put("firstName", "John");
        user.put("lastName", "Kargopoloy");

        //Act
        Map updatedUserDetails = userService.updateUser(createdUserID, user);

        Assertions.assertEquals(user.get("firstName"), updatedUserDetails.get("firstName"));
        Assertions.assertEquals(user.get("lastName"), updatedUserDetails.get("lastName"));
    }

    @Test
    @Order(3)
    @DisplayName("Find user works")
    void testGetUserDetails_whenProvidedWithValidUserId_returnsUserDetails() {
        //Act
        Map userDetails = userService.getUserDetails(createdUserID);

        //Assert
        assertNotNull(userDetails);
        assertEquals(createdUserID, userDetails.get("userId"));
    }

    @Test
    @Order(4)
    @DisplayName("Delete user works")
    void testDeleteUser_whenProvidedWithValidUserId_returnsUserDetails() {
        //Act
        userService.deleteUser(createdUserID);
        
        //Assert
        assertNull(userService.getUserDetails(createdUserID));
    }

}
