package com.mylook.mylook.entities;

import com.google.firebase.Timestamp;

public class Coupon {
    private Timestamp dueDate;
    private Timestamp createdAt;
    private String clientId;
    private String title;
    private String description;
    private String store;
    private String imgStoreUrl;

    public Coupon() {
    }


    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getImgStoreUrl() {
        return imgStoreUrl;
    }

    public void setImgStoreUrl(String imgStoreUrl) {
        this.imgStoreUrl = imgStoreUrl;
    }
}
