package com.vogella.android.myapplication;

import java.io.Serializable;

public class MyUser implements Serializable {
    private int id;

    private String firstName;

    private String lastName;

    private String Email;

    private String Username;

    private String Password;

    private String userType;

    private boolean enabled;

    public MyUser() {

    }

    public MyUser(String email, String username, String password) {
        Email = email;
        Username = username;
        Password = password;
    }

    public MyUser(String email, String username, String password, String firstname, String lastname) {
        Email = email;
        Username = username;
        Password = password;
        firstName = firstname;
        lastName = lastname;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
}
