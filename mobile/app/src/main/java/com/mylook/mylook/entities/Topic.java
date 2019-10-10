package com.mylook.mylook.entities;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Topic implements Serializable {
    private String topic;
    private String userId;
    private Timestamp creationDate;
    private String description;


    public Topic(String topic, String userId, Timestamp creationDate) {
        this.topic = topic;
        this.userId = userId;
        this.creationDate = creationDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public Topic(){

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
