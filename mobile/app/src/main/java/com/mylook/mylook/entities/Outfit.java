package com.mylook.mylook.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Outfit {
    private String name;
    private String category;
    private HashMap<String, String> items;
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

    public HashMap<String, String> getItems() {
        return items;
    }

    public void setItems(HashMap<String, String> items) {
        this.items = items;
    }

    public String getOutfitId() {
        return outfitId;
    }

    public void setOutfitId(String outfitId) {
        this.outfitId = outfitId;
    }
}
