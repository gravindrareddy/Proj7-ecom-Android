package com.hackathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hackathon.buyer.BuyerHomeActivity;
import com.hackathon.seller.SellerHomeActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, BuyerHomeActivity.class));
                finish();
            }
        }, 1000);
    }

    public void buyerOnClick(View view) {
        startActivity(new Intent(SplashActivity.this, BuyerHomeActivity.class));
        finish();
    }

    public void sellerOnClick(View view) {
        startActivity(new Intent(SplashActivity.this, SellerHomeActivity.class));
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
