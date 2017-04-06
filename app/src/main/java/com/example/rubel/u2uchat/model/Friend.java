package com.example.rubel.u2uchat.model;

/**
 * Created by rubel on 4/5/2017.
 */

public class Friend {
    String name;
    String photoUrl;
    boolean isOnline;

    public Friend(String name, String photoUrl, boolean isOnline){
        this.name = name;
        this.photoUrl = photoUrl;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
