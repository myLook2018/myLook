package com.mylook.mylook.entities;

import java.util.ArrayList;

public class Interaction {
    private boolean savedToCloset;
    private boolean liked;
    private boolean clickOnArticle;
    private String articleId;
    private ArrayList<String> tags;
    private String storeName;
    private String userId;

    public Interaction(){

    }

    public Interaction(boolean savedToCloset, boolean liked, String articleId, ArrayList<String> tags, String storeName, String userId) {
        this.savedToCloset = savedToCloset;
        this.liked = liked;
        this.articleId = articleId;
        this.tags = tags;
        this.storeName = storeName;
        this.userId = userId;
    }

    public boolean isSavedToCloset() {
        return savedToCloset;
    }

    public void setSavedToCloset(boolean savedToCloset) {
        this.savedToCloset = savedToCloset;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isClickOnArticle() {
        return clickOnArticle;
    }

    public void setClickOnArticle(boolean clickOnArticle) {
        this.clickOnArticle = clickOnArticle;
    }

    public boolean isValid(){
        return this.clickOnArticle || this.liked || this.savedToCloset;
    }

}
