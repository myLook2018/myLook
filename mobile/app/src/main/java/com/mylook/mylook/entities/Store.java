package com.mylook.mylook.entities;

import java.util.Date;
import java.util.Map;

public class Store {

    private String firebaseUserId;
    private String storeName;
    private String storeMail;
    private String ownerName;
    private String profilePh;
    private String coverPh;
    private String storePhone;
    private String storeProvince;
    private String storeCity;
    private String storeAddress;
    private String storeFloor;
    private Double storeLatitude;
    private Double storeLongitude;
    private String storeTower;
    private String storeDept;
    private String storeDescription;
    private String instagramLink;
    private String twitterLink;
    private String facebookLink;
    private String provider;
    private Date registerDate;

    public Store() {
    }

    public Store(String firebaseUserId, String storeName, String storeMail, String ownerName, String profilePh, String coverPh, String storePhone, String storeProvince, String storeCity, String storeAddress, String storeFloor, Double storeLatitude, Double storeLongitude, String storeTower, String storeDept, String storeDescription, String instagramLink, String twitterLink, String facebookLink, String provider, Date registerDate) {
        this.firebaseUserId = firebaseUserId;
        this.storeName = storeName;
        this.storeMail = storeMail;
        this.ownerName = ownerName;
        this.profilePh = profilePh;
        this.coverPh = coverPh;
        this.storePhone = storePhone;
        this.storeProvince = storeProvince;
        this.storeCity = storeCity;
        this.storeAddress = storeAddress;
        this.storeFloor = storeFloor;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
        this.storeTower = storeTower;
        this.storeDept = storeDept;
        this.storeDescription = storeDescription;
        this.instagramLink = instagramLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.provider = provider;
        this.registerDate = registerDate;
    }

    public String getFirebaseUserId() {
        return firebaseUserId;
    }

    public void setFirebaseUserId(String firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreMail() {
        return storeMail;
    }

    public void setStoreMail(String storeMail) {
        this.storeMail = storeMail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getProfilePh() {
        return profilePh;
    }

    public void setProfilePh(String profilePh) {
        this.profilePh = profilePh;
    }

    public String getCoverPh() {
        return coverPh;
    }

    public void setCoverPh(String coverPh) {
        this.coverPh = coverPh;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreProvince() {
        return storeProvince;
    }

    public void setStoreProvince(String storeProvince) {
        this.storeProvince = storeProvince;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreFloor() {
        return storeFloor;
    }

    public void setStoreFloor(String storeFloor) {
        this.storeFloor = storeFloor;
    }

    public Double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(Double storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public Double getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(Double storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getStoreTower() {
        return storeTower;
    }

    public void setStoreTower(String storeTower) {
        this.storeTower = storeTower;
    }

    public String getStoreDept() {
        return storeDept;
    }

    public void setStoreDept(String storeDept) {
        this.storeDept = storeDept;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Override
    public String toString() {
        return "Store{" +
                "firebaseUserId='" + firebaseUserId + '\'' +
                ", storeName='" + storeName + '\'' +
                ", storeMail='" + storeMail + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", profilePh='" + profilePh + '\'' +
                ", coverPh='" + coverPh + '\'' +
                ", storePhone='" + storePhone + '\'' +
                ", storeProvince='" + storeProvince + '\'' +
                ", storeCity='" + storeCity + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", storeFloor='" + storeFloor + '\'' +
                ", storeLatitude=" + storeLatitude +
                ", storeLongitude=" + storeLongitude +
                ", storeTower='" + storeTower + '\'' +
                ", storeDept='" + storeDept + '\'' +
                ", storeDescription='" + storeDescription + '\'' +
                ", instagramLink='" + instagramLink + '\'' +
                ", twitterLink='" + twitterLink + '\'' +
                ", facebookLink='" + facebookLink + '\'' +
                ", provider='" + provider + '\'' +
                ", registerDate=" + registerDate +
                '}';
    }
}