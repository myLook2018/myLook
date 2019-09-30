package com.mylook.mylook.entities;

import com.google.firebase.Timestamp;

public class DiffusionMessage {
    private String userId;
    private Timestamp creationDate;
    private String message;
    private String imageUrl;
    private String store;
    private String articleId;
    private String documentId;
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public DiffusionMessage() {
    }

    public DiffusionMessage(String userId, Timestamp creationDate, String message, String imageUrl, String store, String articleId) {
        this.userId = userId;
        this.creationDate = creationDate;
        this.message = message;
        this.imageUrl = imageUrl;
        this.store = store;
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "DiffusionMessage{" +
                "userId='" + userId+ '\'' +
                ", creationDate=" + creationDate +
                ", message='" + message + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", store='" + store + '\'' +
                ", articleId='" + articleId + '\'' +
                '}';
    }

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
}
