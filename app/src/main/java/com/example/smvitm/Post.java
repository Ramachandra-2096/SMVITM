package com.example.smvitm;
public class Post {
    private String content;
    private String userId;
    private long timestamp;

    public Post() {
    }

    public Post(String content, String userId, long timestamp) {
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

