package com.junit.estore.service;

import com.junit.estore.data.UserRepository;
import com.junit.estore.model.User;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Mock
    EmailVerificationServiceImpl emailVerificationService;
    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;


    @BeforeEach
    void init() {
        firstName = "Sergey";
        lastName = "Korgloy";
        email = "test@testmcom";
        password = "123456";
        repeatPassword = "123456";
    }

    @Test
    @DisplayName("User object created")
    void testCreateUser_whenUserDetailsProvided_returnUserObject() throws UserServiceException {
        //Arrange
        when(userRepository.save(any(User.class))).thenReturn(true);

        //Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        //Asserts
        assertNotNull(user, "The createUser() method should not return null");
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());
        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    @DisplayName("Empty first name causes correct exceptction")
    void testCreateUser_whenFirstNameIsEmpty_throwsIllegalArgumentException() {
        //Arrange
        String firstName = "";
        String exceptionMessage = "User first name is empty";

        //Act
        //Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Empty first name throws Illegal Argument Exception");
        assertEquals(exceptionMessage, illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("Verifies password length")
    void testCreateUser_whenPasswordIsCorrect_verifyLength() throws UserServiceException {
        //Arrange
        when(userRepository.save(any(User.class))).thenReturn(true);

        //Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);
        int minLength = 6;
        int maxLength = 24;

        assertThat(user.getPassword().length()).isBetween(minLength, maxLength);
    }

    @Test
    @DisplayName("Verifies throws UserServiceException when user not created")
    void testCreateUser_whenUserNotCreated_shouldThrowUserServiceException() throws UserServiceException {
        //Arrange
        when(userRepository.save(any(User.class))).thenReturn(false);

        //Act & Assert
        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        });
    }

    @Test
    @DisplayName("Throws Illegal Argument Exception when password length is incorrect")
    void testCreateUser_whenPasswordIsCorrect_throwsIllegalArgumentExceptionWhenPasswordLengthIsIncorrect() {
        //Arrange
        String password = "123";
        String repeatPassword = "123";
        String exceptionMessage = "Password is invalid";

        //Act & Arrange
        ThrowableAssertAlternative<IllegalArgumentException> exception = assertThatIllegalArgumentException().isThrownBy(() -> userService.createUser(firstName, lastName, email, password, repeatPassword));
        assertThat(exception.actual().getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    @DisplayName("If save() method causes RunTimeException, a UserServiceException is thrown")
    void testCreateUser_whenMethodThrowsException_thenThrowsUserServiceException() {
        //Arrange
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        //Act
        //Assert

        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");
    }

    @Test
    @DisplayName("EmailNotificationException is handled")
    void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException() throws UserServiceException {
        // Arrange
        when(userRepository.save(Mockito.any(User.class))).thenReturn(true);
        doThrow(EmailNotificationServiceException.class)
                .when(emailVerificationService)
                .scheduleEmailConfirmation(any(User.class));
//        doNothing().when(emailVerificationService)
//                .scheduleEmailConfirmation(any(User.class));


        // Act & Assert
        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException");
        verify(emailVerificationService, times(1))
                .scheduleEmailConfirmation(any(User.class));
    }

    @DisplayName("Schedule Email Confirmation is executed")
    @Test
    void testCreateUser_whenUserCreated_schedulesEmailConfirmation() throws UserServiceException {
        //Arrange
        when(userRepository.save(any(User.class))).thenReturn(true);

        doCallRealMethod()
                .when(emailVerificationService)
                .scheduleEmailConfirmation(any(User.class));

        //Act
        userService.createUser(firstName, lastName, email, password, repeatPassword);

        //Assert
        verify(emailVerificationService, times(1))
                .scheduleEmailConfirmation(any(User.class));
    }
}
