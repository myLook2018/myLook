package com.mylook.mylook.entities;

import java.util.ArrayList;
import java.util.List;

public class Outfit {
    private String name;
    private String category;
    private ArrayList<String> items;
    private ArrayList<Article> articles;
    private String outfitId;

    public Outfit() {
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

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public String getOutfitId() {
        return outfitId;
    }

    public void setOutfitId(String outfitId) {
        this.outfitId = outfitId;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles.addAll(articles);
    }
}
