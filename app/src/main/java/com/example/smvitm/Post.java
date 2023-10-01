package com.example.smvitm;

public class Post {
    private String content;
    private String userId;
    private long timestamp;
    private String fileUrl; // New field for the file URL

    public Post() {
    }

    public Post(String content, String userId, long timestamp, String fileUrl) {
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.fileUrl = fileUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
