package com.mylook.mylook.entities;

import java.util.List;

public class Outfit {
    private String name;
    private String category;
    private List<String> items;
    private List<Article> articles;
    private String outfitId;

    public Outfit() {
    }

    public Outfit(String name, String category, List<String> items) {
        this.name = name;
        this.category = category;
        this.items = items;
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

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
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
}
