package com.mylook.mylook.entities;

import java.io.Serializable;
import java.util.Date;

public class PremiumPublication implements Serializable {
    private String articleCode;
    private String clientId;
    private String publicationPhoto;
    private String storeName;
    private String userId;
    private Date date;
    private String premiumPublicationId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PremiumPublication() {
    }

    public PremiumPublication(String articleCode, String clientId, String publicationPhoto, String storeName, String userId, Date date) {
        this.articleCode = articleCode;
        this.clientId = clientId;
        this.publicationPhoto = publicationPhoto;
        this.storeName = storeName;
        this.userId = userId;
        this.date = date;
    }


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


    public String getPremiumPublicationId() {
        return premiumPublicationId;
    }

    public void setPremiumPublicationId(String premiumPublicationId) {
        this.premiumPublicationId = premiumPublicationId;
    }
}
