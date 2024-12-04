package com.example.ugposts.model;

import java.util.List;

public class FacebookPostResponse {
    private List<Post> data;

    // Getters y Setters
    public List<Post> getData() {
        return data;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }
}