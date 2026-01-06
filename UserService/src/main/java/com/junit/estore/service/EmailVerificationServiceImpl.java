package com.junit.estore.service;

import com.junit.estore.model.User;

public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Override
    public void scheduleEmailConfirmation(User user) {
        // Put user details into email queue
        System.out.println("Triggered");
    }
}
