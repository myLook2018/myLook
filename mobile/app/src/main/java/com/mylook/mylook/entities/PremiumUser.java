package com.mylook.mylook.entities;

import java.util.Date;

public class PremiumUser {
    private String clientId;
    private String contactMail;
    private String linkFacebook;
    private String linkInstagram;
    private String localization;
    private Date premiumDate;
    private String profilePhoto;
    private String userId;
    private String userName;

    public PremiumUser() {
    }

    public PremiumUser(String clientId, String contactMail, String linkFacebook, String linkInstagram, String localization, Date premiumDate, String profilePhoto, String userId, String userName) {
        this.clientId = clientId;
        this.contactMail = contactMail;
        this.linkFacebook = linkFacebook;
        this.linkInstagram = linkInstagram;
        this.localization = localization;
        this.premiumDate = premiumDate;
        this.profilePhoto = profilePhoto;
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getLinkFacebook() {
        return linkFacebook;
    }

    public void setLinkFacebook(String linkFacebook) {
        this.linkFacebook = linkFacebook;
    }

    public String getLinkInstagram() {
        return linkInstagram;
    }

    public void setLinkInstagram(String linkInstagram) {
        this.linkInstagram = linkInstagram;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public Date getPremiumDate() {
        return premiumDate;
    }

    public void setPremiumDate(Date premiumDate) {
        this.premiumDate = premiumDate;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
