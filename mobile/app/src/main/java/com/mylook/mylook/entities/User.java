package com.mylook.mylook.entities;

import java.util.HashMap;

public class User implements  Comparable<User> {
    private String name;
    private String surname;
    private String userId;
    private String gender;
    private String email;
    private String dni;
    private long birthday;
    private String installToken;
    private boolean isPremium;

    public User() {
    }

    @Override
    public int compareTo(User o) {
        if(!name.equals(o.name)){
            return 1;
        }
        if(!surname.equals(o.surname)){
            return 1;
        }
        if(!gender.equals(o.gender)){
            return 1;
        }
        if(!email.equals(o.email)){
            return 1;
        }
        if(!dni.equals(o.dni)){
            return 1;
        }
        if(birthday!=o.birthday){
            return 1;
        }
        return 0;
    }

    public User(String name, String surname, String userId, String gender, String email, String dni, long birthday,boolean isPremium) {
        this.name = name;
        this.surname = surname;
        this.userId = userId;
        this.gender = gender;
        this.email = email;
        this.dni = dni;
        this.birthday = birthday;
        this.isPremium=isPremium;
    }

    public boolean getPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getInstallToken() {
        return installToken;
    }

    public void setInstallToken(String installToken) {
        this.installToken = installToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("surname", this.getSurname());
        map.put("email", this.getEmail());
        map.put("gender", this.getGender());
        map.put("dni", this.getDni());
        map.put("userId", this.getUserId());
        map.put("birthday",this.getBirthday());
        map.put("isPremium",this.getPremium());
        return map;
    }
}
