package com.ecommerce.buyer.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ecommerce.R;
import com.ecommerce.buyer.BuyerHomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment implements View.OnClickListener {
    private View view;
    private Activity activity;
    private LinearLayout llAddOrders;
    private ImageView ivBack;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wallet, container, false);
        initControls();
        return view;
    }

    private void initControls() {
        ivBack = (ImageView) view.findViewById(R.id.iv_back_wallet);
        ivBack .setOnClickListener(this);
        llAddOrders = (LinearLayout) view.findViewById(R.id.ll_orders_wallet);
        updateUI();
    }

    private void updateUI() {
        //// TODO: 9/8/2017 call order history
        for (int i = 0; i < 5; i++) {
            RelativeLayout linearLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.tem_for_wallet, null);
            llAddOrders.addView(linearLayout);
        }
    }

    @Override
    public void onClick(View view) {
        if (view==ivBack){
            ((BuyerHomeActivity)activity).onBackPressed();
        }
    }
}
