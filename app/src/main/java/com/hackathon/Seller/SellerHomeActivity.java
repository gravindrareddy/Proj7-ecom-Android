package com.hackathon.seller;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.hackathon.R;
import com.hackathon.seller.fragments.SellerProductListFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SellerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        addFragment(new SellerProductListFragment());
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        final Fragment a = getSupportFragmentManager().findFragmentById(R.id.container);
        if (a instanceof SellerProductListFragment) {
            finish();
        } else {
            super.onBackPressed();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //( (SellerProductListFragment)getSupportFragmentManager().findFragmentById(R.id.container)).fetchCampaigns();
                }
            },2000);
            //addFragment(new SellerProductListFragment());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
