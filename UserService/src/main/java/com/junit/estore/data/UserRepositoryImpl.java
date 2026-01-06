package com.junit.estore.data;

import com.junit.estore.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {
    Map<String, User> users = new HashMap<>();

    @Override
    public boolean save(User user) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }
}
