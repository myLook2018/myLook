package com.mylook.mylook.entities;

import com.google.firebase.Timestamp;

public class Coupon {
    private Timestamp dueDate;
    private Timestamp creationDate;
    private Timestamp usedDate;
    private String clientId;
    private String title;
    private String description;
    private String storeName;
    private String imgStoreUrl;
    private String storeId;
    private String gender;
    private String installToken;
    private String code;
    private String documentId;
    private int dni;
    private int age;
    private int voucherType;
    private boolean used;
    private boolean suscript;


    public Coupon() {
    }

    public int getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(int voucherType) {
        this.voucherType = voucherType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public Timestamp getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Timestamp usedDate) {
        this.usedDate = usedDate;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInstallToken() {
        return installToken;
    }

    public void setInstallToken(String installToken) {
        this.installToken = installToken;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isSuscript() {
        return suscript;
    }

    public void setSuscript(boolean suscript) {
        this.suscript = suscript;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getImgStoreUrl() {
        return imgStoreUrl;
    }

    public void setImgStoreUrl(String imgStoreUrl) {
        this.imgStoreUrl = imgStoreUrl;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "dueDate=" + dueDate +
                ", creationDate=" + creationDate +
                ", usedDate=" + usedDate +
                ", clientId='" + clientId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", storeName='" + storeName + '\'' +
                ", imgStoreUrl='" + imgStoreUrl + '\'' +
                ", storeId='" + storeId + '\'' +
                ", gender='" + gender + '\'' +
                ", installToken='" + installToken + '\'' +
                ", code='" + code + '\'' +
                ", documentId='" + documentId + '\'' +
                ", dni=" + dni +
                ", age=" + age +
                ", used=" + used +
                ", suscript=" + suscript +
                '}';
    }
}
