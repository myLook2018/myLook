package com.mylook.mylook.entities;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Visit {

    private  Timestamp interactionTime;
    private String storeName;
    private String userId;


    public Visit() {
        Calendar cal = Calendar.getInstance();
        this.interactionTime = new Timestamp(cal.getTime());

    }

    public Visit(String storeName, String userId) {
        this.storeName = storeName;
        this.userId = userId;
        Calendar cal = Calendar.getInstance();
        this.interactionTime = new Timestamp(cal.getTime());
    }

    public Timestamp getInteractionTime() {
        return interactionTime;
    }

    public void setInteractionTime(Timestamp interactionTime) {
        this.interactionTime = interactionTime;
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


    public Map<String,Object> toMap(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("storeName",storeName);
        map.put("userId",userId);
        map.put("interactionTime",interactionTime);
        return map;
    }
}
