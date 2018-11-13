package com.mylook.mylook.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class PremiumPublication implements Parcelable {
    private String articleCode;
    private String clientId;
    private String publicationPhoto;
    private String storeNme;
    private String userId;
    private Date date;

    public PremiumPublication(Parcel in) {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PremiumPublication() {
    }

    public PremiumPublication(String articleCode, String clientId, String publicationPhoto, String storeNme, String userId, Date date) {
        this.articleCode = articleCode;
        this.clientId = clientId;
        this.publicationPhoto = publicationPhoto;
        this.storeNme = storeNme;
        this.userId = userId;
        this.date = date;
    }

    public static final Creator<PremiumPublication> CREATOR = new Creator<PremiumPublication>() {
        @Override
        public PremiumPublication createFromParcel(Parcel in) {
            return new PremiumPublication(in);
        }

        @Override
        public PremiumPublication[] newArray(int size) {
            return new PremiumPublication[size];
        }
    };

    public String getArticleCode() {
        return articleCode;
    }

    public void setArticleCode(String articleCode) {
        this.articleCode = articleCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPublicationPhoto() {
        return publicationPhoto;
    }

    public void setPublicationPhoto(String publicationPhoto) {
        this.publicationPhoto = publicationPhoto;
    }

    public String getStoreNme() {
        return storeNme;
    }

    public void setStoreNme(String storeNme) {
        this.storeNme = storeNme;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleCode);
        dest.writeString(clientId);
        dest.writeString(publicationPhoto);
        dest.writeString(storeNme);
        dest.writeString(userId);
    }
}
