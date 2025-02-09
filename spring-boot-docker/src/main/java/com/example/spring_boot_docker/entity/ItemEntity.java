package com.example.spring_boot_docker.entity;

import java.lang.reflect.Array;

public class ItemEntity {


    private String shortDescription;
    private String price;


    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
