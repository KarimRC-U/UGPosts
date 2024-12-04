package com.example.ugposts.model;

public class Post {
    private String message;
    private String created_time;

    public Post(String message, String created_time) {
        this.message = message;
        this.created_time = created_time;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }
}
