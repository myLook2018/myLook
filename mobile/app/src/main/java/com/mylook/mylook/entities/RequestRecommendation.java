package com.mylook.mylook.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestRecommendation implements Serializable {

        private String description;
        private long limitDate;
        private ArrayList<Double> localization;
        private String requestPhoto;
        private boolean isClosed;
        private String updateDate;
        private String userId;
        private String title;
        private ArrayList<ArrayList<String>> answers;

        public RequestRecommendation() {}

    public ArrayList<ArrayList<String>> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<ArrayList<String>> answers) {
        this.answers = answers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(long limitDate) {
        this.limitDate = limitDate;
    }

    public ArrayList<Double> getLocalization() {
        return localization;
    }

    public void setLocalization(ArrayList<Double> localization) {
        this.localization = localization;
    }

    public String getRequestPhoto() {
        return requestPhoto;
    }

    public void setRequestPhoto(String requestPhoto) {
        this.requestPhoto = requestPhoto;
    }

    public boolean getClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        this.isClosed = closed;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
