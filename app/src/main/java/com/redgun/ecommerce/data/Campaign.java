package com.ecommerce.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by gravi on 07-09-2017.
 */
@IgnoreExtraProperties
public class Campaign {

    public String name;
    public double price;
    public double baseDiscount;
    public double maxDiscount;
    public int totalQuantity;
//    public String startDate;
//    public String endDate;
    public String id;
    public boolean isActive;
    public int availableQuantity;
    public String imageURL;

    //use the below to update cashback for each order once Campaign is closed
    private ArrayList<String> ordersByBuyers;


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }




    public int getTotalQuantity() {
        return totalQuantity;
    }

    public ArrayList<String> getOrdersByBuyers() {
        return ordersByBuyers;
    }

    public void setOrdersByBuyers(ArrayList<String> ordersByBuyers) {
        this.ordersByBuyers = ordersByBuyers;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public boolean isActive() {
        return isActive;

    }

    public void setActive(boolean active) {
        isActive = active;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBaseDiscount() {
        return baseDiscount;
    }

    public void setBaseDiscount(double baseDiscount) {
        this.baseDiscount = baseDiscount;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

//    public String getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(String startDate) {
//        this.startDate = startDate;
//    }
//
//    public String getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(String endDate) {
//        this.endDate = endDate;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
