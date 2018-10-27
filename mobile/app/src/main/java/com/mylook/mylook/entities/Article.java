package com.mylook.mylook.entities;

import java.util.ArrayList;

public class Article {

    private ArrayList<String> colors;
    private long cost;
    private long initial_stock;
    private String material;
    private String provider;
    private ArrayList<String> size;
    private String storeName;
    private ArrayList<String> tags;
    private String picture;
    private String title;

    public Article() {}

    public Article(ArrayList<String> colors, long cost, long initial_stock, String material, String provider, ArrayList<String>  size, String storeName, ArrayList<String> tags, String picture) {
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

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
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

    public ArrayList<String>  getSize() {
        return size;
    }

    public void setSize(ArrayList<String>  size) {
        this.size = size;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public long getInitial_stock() {
        return initial_stock;
    }

    public void setInitial_stock(long initial_stock) {
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
