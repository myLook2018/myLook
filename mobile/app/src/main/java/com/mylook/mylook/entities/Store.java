package com.mylook.mylook.entities;

import com.mylook.mylook.entities.storeFront.StoreFront;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Store {

    private String coverPh;
    private String facebookLink;
    private String firebaseUserId;
    private String instagramLink;
    private String ownerName;
    private String profilePh;
    private String provider;
    private String storeAddress;
    private String storeDept;
    private String storeDescription;
    private String storeFloor;
    private double storeLatitude;
    private double storeLongitude;
    private String storeMail;
    private String storeName;
    private String storePhone;
    private String storeTower;
    private String storeProvince;
    private String storeCity;
    private String storeAddressNumber; //TODO este atributo no esta en bd
    private String twitterLink;
    private ArrayList<StoreFront> storeFronts; //Este atributo se agrega para permitir las multiples vidrieras
    private Date registerDate; //TODO este atributo no esta en bd

    public Store() {
    }

    public Store(String coverPh, String facebookLink, String firebaseUserId, String instagramLink, String ownerName, String profilePh, String provider, String storeAddress, String storeDept, String storeDescription, String storeFloor, double storeLatitude, double storeLongitude, String storeMail, String storeName, String storePhone, String storeTower, String storeProvince, String storeCity, String storeAddressNumber, String twitterLink, ArrayList<StoreFront> storeFronts, Date registerDate) {
        this.coverPh = coverPh;
        this.facebookLink = facebookLink;
        this.firebaseUserId = firebaseUserId;
        this.instagramLink = instagramLink;
        this.ownerName = ownerName;
        this.profilePh = profilePh;
        this.provider = provider;
        this.storeAddress = storeAddress;
        this.storeDept = storeDept;
        this.storeDescription = storeDescription;
        this.storeFloor = storeFloor;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
        this.storeMail = storeMail;
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.storeTower = storeTower;
        this.storeProvince = storeProvince;
        this.storeCity = storeCity;
        this.storeAddressNumber = storeAddressNumber;
        this.twitterLink = twitterLink;
        this.storeFronts = storeFronts;
        this.registerDate = registerDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getStorePhone() {
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

    public String getStoreProvince() {
        return storeProvince;
    }

    public void setStorePhone(String storePhone) {
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

    public double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(long storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public double getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(long storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getStoreDept() {
        return storeDept;
    }

    public void setStoreDept(String storeDept) {
        this.storeDept = storeDept;
    }

    public String getStoreTower() {
        return storeTower;
    }

    public void setStoreTower(String storeTower) {
        this.storeTower = storeTower;
    }

    public ArrayList<StoreFront> getStoreFronts() {
        return storeFronts;
    }

    public void setStoreFronts(ArrayList<StoreFront> storeFronts) {
        this.storeFronts = storeFronts;
    }
}