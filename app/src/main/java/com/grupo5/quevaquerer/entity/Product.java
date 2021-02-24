package com.grupo5.quevaquerer.entity;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String image;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private int available;
    private Category category;

    public Product() {
    }

    public Product(int id, String image, String name, String description, int quantity, double price, int available, Category category) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.available = available;
        this.category = category;
    }

    public Product(int id, String image, String name, String description, int quantity, double price, Category category) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    public Product(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Product(String image, String name, String description, int quantity, double price, Category category) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    public Product(int id, String image, String name, double price, Category category) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Product(String image, String name, double price, Category category) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Product(String image, String name, double price) {
        this.image = image;
        this.name = name;
        this.price = price;
    }

    public Product(int id, String image, String name, int quantity) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.quantity = quantity;
    }

    public Product(int id, String image, String name, int quantity, double price) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
