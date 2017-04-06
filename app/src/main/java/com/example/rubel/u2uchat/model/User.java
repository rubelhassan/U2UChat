package com.example.rubel.u2uchat.model;

/**
 * Created by rubel on 4/6/2017.
 */

public class User {
    String userName;
    String email;
    String fullName;
    String uid;
    String photoUrl;
    boolean isOnline;

    public User(String userName, String email, String fullName, String uid,
                String photoUrl, boolean isOnline) {
        this.userName = userName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.isOnline = isOnline;
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
