package com.mylook.mylook.entities;

import java.util.ArrayList;

public class Article {

    private String colors;
    private String cost;
    private String initial_stock;
    private String material;
    private String provider;
    private String size;
    private String storeName;
    private ArrayList<String> tags;
    private String picture;
    private String title;

    public Article() {}

    public Article(String colors, String cost, String initial_stock, String material, String provider, String size, String storeName, ArrayList<String> tags, String picture) {
        this.colors = colors;
        this.cost = cost;
        this.initial_stock = initial_stock;
        this.material = material;
        this.provider = provider;
        this.size = size;
        this.storeName = storeName;
        this.tags = tags;
        this.picture = picture;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getInitial_stock() {
        return initial_stock;
    }

    public void setInitial_stock(String initial_stock) {
        this.initial_stock = initial_stock;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
