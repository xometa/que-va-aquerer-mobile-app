package com.grupo5.quevaquerer.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class OrderHeader implements Serializable {
    private int idorderheader;
    private int idclient;
    private double latitude;
    private double longitude;
    private Date dateorder;
    private double totalorder;
    private int statusorder;
    private Client client;
    private ArrayList<DetailsOrder> detailsOrders;
    private boolean expanded;

    public OrderHeader() {
    }

    public OrderHeader(int idorderheader, int idclient, double latitude, double longitude, Date dateorder, double totalorder, int statusorder, Client client, ArrayList<DetailsOrder> detailsOrders) {
        this.idorderheader = idorderheader;
        this.idclient = idclient;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateorder = dateorder;
        this.totalorder = totalorder;
        this.statusorder = statusorder;
        this.client = client;
        this.detailsOrders = detailsOrders;
        this.expanded = false;
    }

    public OrderHeader(int idorderheader, double latitude, double longitude) {
        this.idorderheader = idorderheader;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public OrderHeader(int idorderheader, int statusorder) {
        this.idorderheader = idorderheader;
        this.statusorder = statusorder;
    }

    public OrderHeader(int idorderheader, int idclient, double latitude, double longitude, Date dateorder, double totalorder, int statusorder, Client client) {
        this.idorderheader = idorderheader;
        this.idclient = idclient;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateorder = dateorder;
        this.totalorder = totalorder;
        this.statusorder = statusorder;
        this.client = client;
    }

    public int getIdorderheader() {
        return idorderheader;
    }

    public void setIdorderheader(int idorderheader) {
        this.idorderheader = idorderheader;
    }

    public int getIdclient() {
        return idclient;
    }

    public void setIdclient(int idclient) {
        this.idclient = idclient;
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

    public Date getDateorder() {
        return dateorder;
    }

    public void setDateorder(Date dateorder) {
        this.dateorder = dateorder;
    }

    public double getTotalorder() {
        return totalorder;
    }

    public void setTotalorder(double totalorder) {
        this.totalorder = totalorder;
    }

    public int getStatusorder() {
        return statusorder;
    }

    public void setStatusorder(int statusorder) {
        this.statusorder = statusorder;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ArrayList<DetailsOrder> getDetailsOrders() {
        return detailsOrders;
    }

    public void setDetailsOrders(ArrayList<DetailsOrder> detailsOrders) {
        this.detailsOrders = detailsOrders;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
