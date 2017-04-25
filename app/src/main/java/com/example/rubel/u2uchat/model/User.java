package com.example.rubel.u2uchat.model;

import java.io.Serializable;

/**
 * Created by rubel on 4/6/2017.
 */

public class User implements Serializable {
    String userName;
    String email;
    String fullName;
    String uid;
    String photoUrl;
    String sex;
    int age;
    boolean isOnline;

    public User() {
    }

    public User(String userName, String email, String fullName, String uid, String sex, int age,
                String photoUrl, boolean isOnline) {
        this.userName = userName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.isOnline = isOnline;
        this.fullName = fullName;
        this.sex = sex;
        this.age = age;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGeoId() {
        return this.uid + ";" + this.fullName.replaceAll(" ", "*");
        // TODO add age, sex, and photourl
    }
}
