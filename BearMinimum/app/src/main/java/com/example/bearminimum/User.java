package com.example.bearminimum;

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

    public String getEmail() {
        return email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
