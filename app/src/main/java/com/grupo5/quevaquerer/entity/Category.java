package com.grupo5.quevaquerer.entity;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String image;
    private String name;
    private String description;

    public Category() {
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(int id, String image, String name, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
    }

    public Category(int id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public Category(String image, String name, String description) {
        this.image = image;
        this.name = name;
        this.description = description;
    }

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
