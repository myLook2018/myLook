package com.mylook.mylook.room;

import androidx.room.Entity;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(primaryKeys = {"uid", "userId"}, tableName = "localinteractions")
public class LocalInteraction {

    public LocalInteraction() { }

    private @NonNull String  uid;

    private @NonNull String userId;

    private Date date;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
