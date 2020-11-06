package com.example.bearminimum;

/**
 * User
 *
 * Creates a user object to store user information
 *
 * Nov. 6, 2020
 */

public class User {
    private String email;
    private String phonenumber;
    private String uid;
    private String username;

    public User(String email, String phonenumber, String uid, String username) {
        this.email = email;
        this.phonenumber = phonenumber;
        this.uid = uid;
        this.username = username;
    }

    /**
     * returns user's email
     *
     * @return email    a String type of user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * returns user's phone number
     *
     * @return phonenumber    a String type of user's phonenumber
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * returns user's user id
     *
     * @return uid    a String type of user's user id
     */
    public String getUid() {
        return uid;
    }

    /**
     * returns user's username
     *
     * @return username    a String type of user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Overwrites user's current email to the provided email
     *
     * @param email     the new email to save
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Overwrites user's current phone number to the provided
     * phone number
     *
     * @param phonenumber     the new phone number to save
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * Overwrites user's current user id to the provided user id
     *
     * @param uid     the new user id to save
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Overwrites user's current username to the provided username
     *
     * @param username     the new username to save
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
