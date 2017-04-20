package com.example.rubel.u2uchat.model;

/**
 * Created by rubel on 4/18/2017.
 */


public class Message {

    String content;
    String sender;
    String receiver;
    boolean text;
    boolean left;
    long timestamps;

    public Message(String content, String sender, String receiver, boolean text, boolean left, long timestamps) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.left = left;
        this.timestamps = timestamps;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(long timestamps) {
        this.timestamps = timestamps;
    }
}
