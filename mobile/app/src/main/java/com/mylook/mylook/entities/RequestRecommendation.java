package com.mylook.mylook.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestRecommendation implements Serializable {

        private String description;
        private long limitDate;
        private ArrayList<Double> localization;
        private String requestPhoto;
        private boolean isClosed;
        private String updateDate;
        private String userId;
        private String title;
        private ArrayList<HashMap<String,String>> answers;
        private String documentId;
        private String size;
        private String category;

        public RequestRecommendation() {}

    public ArrayList<HashMap<String,String>> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<HashMap<String,String>> answers) {
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

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "RequestRecommendation{" +
                "description='" + description + '\'' +
                ", limitDate=" + limitDate +
                ", localization=" + localization +
                ", requestPhoto='" + requestPhoto + '\'' +
                ", isClosed=" + isClosed +
                ", updateDate='" + updateDate + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", answers=" + answers +
                ", documentId='" + documentId + '\'' +
                ", size='" + size + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
