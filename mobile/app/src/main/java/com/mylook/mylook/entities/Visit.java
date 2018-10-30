package com.mylook.mylook.entities;

import java.util.HashMap;
import java.util.Map;

public class Visit {

    private String storeName;
    private String userId;
    private long count;

    public Visit() {
    }

    public Visit(String storeName, String userId, long count) {
        this.storeName = storeName;
        this.userId = userId;
        this.count = count;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
    public void toVisit(){
        count++;
    }
    public Map<String,Object> toMap(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("storeName",storeName);
        map.put("userId",userId);
        map.put("count",count);
        return map;
    }
}
