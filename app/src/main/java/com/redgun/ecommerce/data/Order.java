package com.ecommerce.data;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by gravi on 07-09-2017.
 */
@IgnoreExtraProperties
public class Order {


    private String id;
    private String campaignId;
    private double price;
    private int orderQuantity;
    //this will help us to determine whether the user is an early bird and eligible for extra discount
    private int purchaseSequence;
    private String userId;
    private double cashbackReceived;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public double getCashbackReceived() {
        return cashbackReceived;
    }

    public void setCashbackReceived(double cashbackReceived) {
        this.cashbackReceived = cashbackReceived;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public int getPurchaseSequence() {
        return purchaseSequence;
    }

    public void setPurchaseSequence(int purchaseSequence) {
        this.purchaseSequence = purchaseSequence;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
