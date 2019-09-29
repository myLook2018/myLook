package com.mylook.mylook.entities.storeFront;


import java.util.ArrayList;

public class StoreFront {

    private int id;
    private ArrayList<String> articlesIdInStoreFront;
    private boolean isActive;
    private String storeFrontName;

    public StoreFront() {
    }

    public StoreFront(int id, ArrayList<String> articlesIdInStoreFront, boolean isActive, String storeFrontName) {
        this.id = id;
        this.articlesIdInStoreFront = articlesIdInStoreFront;
        this.isActive = isActive;
        this.storeFrontName = storeFrontName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getArticlesIdInStoreFront() {
        return articlesIdInStoreFront;
    }

    public void setArticlesIdInStoreFront(ArrayList<String> articlesIdInStoreFront) {
        this.articlesIdInStoreFront = articlesIdInStoreFront;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStoreFrontName() {
        return storeFrontName;
    }

    public void setStoreFrontName(String storeFrontName) {
        this.storeFrontName = storeFrontName;
    }
}
