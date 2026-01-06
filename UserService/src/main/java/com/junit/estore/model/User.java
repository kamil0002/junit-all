package com.junit.estore.model;

public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    String password;
    String repeatPassword;


    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public User(String id, String firstName, String lastName, String email, String password, String repeatPassword) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }
}
