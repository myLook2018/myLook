package com.mylook.mylook.entities;

import java.io.Serializable;
import java.util.List;

public class Outfit implements Serializable {
    private String name;
    private String category;
    private List<String> favorites;
    private List<Article> articles;
    private String outfitId;
    private String userID;

    public Outfit() {
    }

    public Outfit(String name, String category, List<String> favorites) {
        this.name = name;
        this.category = category;
        this.favorites = favorites;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public String getOutfitId() {
        return outfitId;
    }

    public void setOutfitId(String outfitId) {
        this.outfitId = outfitId;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}