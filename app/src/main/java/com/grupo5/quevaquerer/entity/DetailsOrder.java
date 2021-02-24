package com.grupo5.quevaquerer.entity;

import java.io.Serializable;

public class DetailsOrder implements Serializable {
    private int iddetailsorder;
    private int idorderheader;
    private int idproduct;
    private double price;
    private int quantity;
    private OrderHeader orderHeader;
    private Product product;

    public DetailsOrder() {
    }

    public DetailsOrder(int iddetailsorder, int idorderheader, int idproduct, double price, int quantity, OrderHeader orderHeader, Product product) {
        this.iddetailsorder = iddetailsorder;
        this.idorderheader = idorderheader;
        this.idproduct = idproduct;
        this.price = price;
        this.quantity = quantity;
        this.orderHeader = orderHeader;
        this.product = product;
    }

    public DetailsOrder(int iddetailsorder, int idorderheader, int idproduct, double price, int quantity, Product product) {
        this.iddetailsorder = iddetailsorder;
        this.idorderheader = idorderheader;
        this.idproduct = idproduct;
        this.price = price;
        this.quantity = quantity;
        this.product = product;
    }

    public DetailsOrder(int iddetailsorder, int idproduct, double price, int quantity, Product product) {
        this.iddetailsorder = iddetailsorder;
        this.idproduct = idproduct;
        this.price = price;
        this.quantity = quantity;
        this.product = product;
    }

    public int getIddetailsorder() {
        return iddetailsorder;
    }

    public void setIddetailsorder(int iddetailsorder) {
        this.iddetailsorder = iddetailsorder;
    }

    public int getIdorderheader() {
        return idorderheader;
    }

    public void setIdorderheader(int idorderheader) {
        this.idorderheader = idorderheader;
    }

    public int getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(int idproduct) {
        this.idproduct = idproduct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderHeader getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(OrderHeader orderHeader) {
        this.orderHeader = orderHeader;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
