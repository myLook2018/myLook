package com.mylook.mylook.entities;

import com.google.firebase.Timestamp;

public class Notification {
    private String userId;
    private Timestamp creationDate;
    private String message;
    private String imageUrl;
    private String store;
    private String articleId;
    private String documentId;
    private String topic;
    private String userPhotoUrl;
    private String premiumUserName;
    private boolean openedNotification;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getPremiumUserName() {
        return premiumUserName;
    }

    public void setPremiumUserName(String premiumUserName) {
        this.premiumUserName = premiumUserName;
    }

    public boolean isOpenedNotification() {
        return openedNotification;
    }

    public void setOpenedNotification(boolean openedNotification) {
        this.openedNotification = openedNotification;
    }
}
