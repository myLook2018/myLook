package com.mylook.mylook.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Article implements Serializable{

    private String articleId;
    private String title;
    private String code;
    private String provider;
    private float cost;
    private int initial_stock;
    private String material;
    private String picture;
    private String storeName;
    private ArrayList<String> colors;
    private ArrayList<String> sizes;
    private ArrayList<String> tags;

    public Article() {
    }

    public Article(String articleId, String title, String code, String provider, float cost, int initial_stock, String material, String picture, String storeName, ArrayList<String> colors, ArrayList<String> sizes, ArrayList<String> tags) {
        this.articleId = articleId;
        this.title = title;
        this.code = code;
        this.provider = provider;
        this.cost = cost;
        this.initial_stock = initial_stock;
        this.material = material;
        this.picture = picture;
        this.storeName = storeName;
        this.colors = colors;
        this.sizes = sizes;
        this.tags = tags;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getInitial_stock() {
        return initial_stock;
    }

    public void setInitial_stock(int initial_stock) {
        this.initial_stock=initial_stock;
    }
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public ArrayList<String> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.sizes = sizes;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}