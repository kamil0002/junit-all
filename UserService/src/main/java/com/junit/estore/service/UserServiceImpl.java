package com.junit.estore.service;

import com.junit.estore.data.UserRepository;
import com.junit.estore.model.User;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

    public UserServiceImpl(UserRepository userRepository,
                           EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public User createUser(String firstName, String lastName, String email, String password, String repeatPassword) throws UserServiceException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("User first name is empty");
        }
        if (password.length() < 6 || password.length() > 24) {
            throw new IllegalArgumentException("Password is invalid");
        }
        User user = new User(UUID.randomUUID().toString(), firstName, lastName, email, password, repeatPassword);
        boolean created;
        try {
            created = userRepository.save(user);
        } catch (RuntimeException ex) {
            throw new UserServiceException(ex.getMessage());
        }
        if (!created) {
            throw new UserServiceException("Could not create user");
        }
        try {
            emailVerificationService.scheduleEmailConfirmation(user);
        } catch (RuntimeException ex) {
            throw new UserServiceException(ex.getMessage());
        }
        return user;
    }
}
