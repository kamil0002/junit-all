package com.junit.estore.data;

import com.junit.estore.model.User;

public interface UserRepository {
    boolean save(User user);
}
