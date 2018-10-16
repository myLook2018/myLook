package com.mylook.mylook.entities;

public class Subscription {

    private String storeName;
    private String userId;

    public Subscription() {
    }

    public Subscription(String storeName, String userId) {
        this.storeName = storeName;
        this.userId = userId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
