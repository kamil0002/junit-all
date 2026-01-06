package com.junit.estore.service;

import com.junit.estore.model.User;

public interface EmailVerificationService {
    void scheduleEmailConfirmation(User user);
}
