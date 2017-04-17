package com.example.rubel.u2uchat.model;

/**
 * Created by rubel on 4/14/2017.
 */

public class UserConnection {
    private String name;
    private String messageId;
    private String senderId;
    private String lastMessage;
    private long timestamps;
    private String photoUrl;
    private boolean isOnline;

    public UserConnection(String name, String messageId, String senderId, String lastMessage,
                          long timestamps, String photoUrl, boolean isOnline) {
        this.name = name;
        this.messageId = messageId;
        this.senderId = senderId;
        this.lastMessage = lastMessage;
        this.timestamps = timestamps;
        this.photoUrl = photoUrl;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(long timstamps) {
        this.timestamps = timstamps;
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
