package com.ecommerce.data;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by gravi on 07-09-2017.
 */
@IgnoreExtraProperties
public class User {

    private String name;
    private String email;
    private String role;
    private double walletAmount;

    private String[] campaignsAsSeller;

    public String[] getOrdersAsBuyer() {
        return ordersAsBuyer;
    }

    //Use below to fetch wallet history
    private String[] ordersAsBuyer;


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setOrdersAsBuyer(String[] ordersAsBuyer) {
        this.ordersAsBuyer = ordersAsBuyer;
    }



    public double getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(double walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String[] getCampaignsAsSeller() {
        return campaignsAsSeller;
    }

    public void setCampaignsAsSeller(String[] campaignsAsSeller) {
        this.campaignsAsSeller = campaignsAsSeller;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }


}
