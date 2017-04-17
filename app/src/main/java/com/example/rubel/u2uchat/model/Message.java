package com.example.rubel.u2uchat.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by rubel on 4/18/2017.
 */

@IgnoreExtraProperties
public class Message {

    String content;
    String sender;
    String receiver;
    boolean isText;
    boolean isLeft;
    long timestamps;

    public Message(String content, String sender, String receiver, boolean isText, boolean isLeft,
                   long timestamps) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.isText = isText;
        this.isLeft = isLeft;
        this.timestamps = timestamps;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(long timestamps) {
        this.timestamps = timestamps;
    }
}
