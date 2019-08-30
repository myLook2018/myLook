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
    private long storePhone;
    private String storeProvince;
    private String storeCity;
    private String storeAddress;
    private String storeAddressNumber;
    private String storeFloor;
    private Map<String, Long> storePosition;
    private String storeDescription;
    private String instagramLink;
    private String twitterLink;
    private String facebookLink;
    private String provider;
    private Date registerDate;

    public Store(){}

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public long getStorePhone() {
        return storePhone;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreMail() {
        return storeMail;
    }

    public String getStoreFloor() {
        return storeFloor;
    }

    public String getStoreDescription() {
        return storeDescription;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getStoreAddressNumber() {
        return storeAddressNumber;
    }

    public String getStoreCity() {
        return storeCity;
    }


    public Map getStorePosition() {
        return storePosition;
    }

    public String getStoreProvince() {
        return storeProvince;
    }


    public void setStorePhone(long storePhone) {
        this.storePhone = storePhone;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreMail(String storeMail) {
        this.storeMail = storeMail;
    }

    public void setStoreFloor(String storeFloor) {
        this.storeFloor = storeFloor;
    }


    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public void setStoreAddressNumber(String storeAddressNumber) {
        this.storeAddressNumber = storeAddressNumber;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public void setStorePosition(Map<String,Long> storePosition) {
        this.storePosition = storePosition;
    }

    public void setStoreProvince(String storeProvince) {
        this.storeProvince = storeProvince;
    }

    public String getFirebaseUserId() {
        return firebaseUserId;
    }

    public void setFirebaseUserId(String firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
    }

    public String getCoverPh() {
        return coverPh;
    }

    public String getProfilePh() {
        return profilePh;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public String getProvider() {
        return provider;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setCoverPh(String coverPh) {
        this.coverPh = coverPh;
    }

    public void setProfilePh(String profilePh) {
        this.profilePh = profilePh;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }
}