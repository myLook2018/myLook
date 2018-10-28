package com.mylook.mylook.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Closet implements Serializable {
    private String userID;
    private Collection<Favorite> favorites;
    private ArrayList<String> collections;

    public Closet(String userID, Collection<Favorite> favorites, ArrayList<String> collections) {
        this.userID = userID;
        this.favorites = favorites;
        this.collections = collections;
    }

    public Closet(String userID) {
        this.userID = userID;
        this.collections=new ArrayList<String>();
    }

    public Closet() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Collection<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(Collection<Favorite> favorites) {
        this.favorites = favorites;
    }

    public ArrayList<String> getCollections() {
        return collections;
    }

    public void setCollections(ArrayList<String> collections) {
        this.collections = collections;
    }



}
