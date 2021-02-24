package com.grupo5.quevaquerer.entity;

import java.io.Serializable;

public class Client implements Serializable {
    private int id;
    private String name;
    private String lastname;
    private String image;
    private String phone;
    private double latitude;
    private double longitude;
    private String username;
    private String userpassword;
    private int rol;

    public Client() {
    }

    public Client(int id, String name, String lastname, String image, String phone, double latitude, double longitude, String username, String userpassword, int rol) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.image = image;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
        this.userpassword = userpassword;
        this.rol = rol;
    }

    public Client(int id, String name, String lastname, String image, String phone) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.image = image;
        this.phone = phone;
    }

    public Client(int id, String name, String lastname, String image, String phone, String username, String userpassword) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.image = image;
        this.phone = phone;
        this.username = username;
        this.userpassword = userpassword;
    }

    public Client(int id) {
        this.id = id;
    }

    public Client(String name, String lastname) {
        this.name = name;
        this.lastname = lastname;
    }

    public Client(int id, String name, String lastname, String image) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return name + " " + lastname;
    }
}
