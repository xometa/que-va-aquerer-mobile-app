package com.grupo5.quevaquerer.entity;

import java.io.Serializable;

public class Money implements Serializable {
    String day;
    double money;

    public Money() {
    }

    public Money(String day, double money) {
        this.day = day;
        this.money = money;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
