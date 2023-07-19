package com.example.smvitm;


import java.io.Serializable;

public class Message implements Serializable {

    private String key;
    private String content;
    private String sender;
    private boolean isRead;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String content, String sender, boolean isRead) {
        this.content = content;
        this.sender = sender;
        this.isRead = isRead;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
