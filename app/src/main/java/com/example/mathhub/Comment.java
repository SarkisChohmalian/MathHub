package com.example.mathhub;

import com.google.firebase.database.PropertyName;

public class Comment {

    @PropertyName("userId")
    private String userId;

    @PropertyName("text")
    private String text;

    public Comment() {
    }

    public Comment(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
