package com.ecommerce.seller.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ecommerce.R;
import com.ecommerce.SignInActivity;
import com.ecommerce.buyer.BuyerHomeActivity;
import com.ecommerce.data.Campaign;
import com.ecommerce.data.Order;
import com.ecommerce.seller.SellerHomeActivity;
import com.ecommerce.utils.StringConstants;

import java.util.ArrayList;

public class SellerProductDetailsFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private ImageView ivBack;
    private View view;
    private boolean isBuyerHomeActivity = false;
    private Button btnCreate;
    private TextView tvProductName, tvSubProductName, tvPrice, tvActualPrice, baseDiscount, maxDiscount, tvTotalQuantity;
    private EditText etQuantity;
    private SeekBar seekbar;
    private String TAG = "SellerProductDetailsFragment.java";


    public SellerProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        if (activity.getClass().toString().contains("BuyerHomeActivity")) {
            isBuyerHomeActivity = true;
        } else {
            isBuyerHomeActivity = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PreferenceManager.getDefaultSharedPreferences(activity).contains(StringConstants.PREF_USERNAME))
            createOrder(new Order());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_seller_product_details, container, false);
        initControls();
        return view;
    }

    private void initControls() {
        ivBack = (ImageView) view.findViewById(R.id.iv_back_details);
        btnCreate = (Button) view.findViewById(R.id.btn_bye_details);
        tvProductName = (TextView) view.findViewById(R.id.tv_productName_details);
        tvSubProductName = (TextView) view.findViewById(R.id.tv_subProductName_details);
        tvPrice = (TextView) view.findViewById(R.id.tv_price_details);
        tvActualPrice = (TextView) view.findViewById(R.id.tv_actualPrice_details);
        baseDiscount = (TextView) view.findViewById(R.id.tv_base_discount_productName_details);
        maxDiscount = (TextView) view.findViewById(R.id.tv_total_discount_productName_details);
        tvTotalQuantity = (TextView) view.findViewById(R.id.tv_total_units_productName_details);
        etQuantity = (EditText) view.findViewById(R.id.et_quantity_details);
        seekbar = (SeekBar) view.findViewById(R.id.seekBar_productName_details);
        if (!isBuyerHomeActivity) {
            btnCreate.setText("Close Sale");
            etQuantity.setVisibility(View.GONE);
        }
        ivBack.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        fetchCampaign(getArguments().getString("bundleOrderId"));
    }

//    private void fetchOrder(String orderId) {
//        final ArrayList<Order> orders = new ArrayList<Order>();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");
//        Query ordersQuery = ref.orderByChild(orderId);
//        ordersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
//                    Order tempOrder = orderSnapshot.getValue(Order.class);
//                    orders.add(tempOrder);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//    }


    //todo: fetching multiple campains.. fix it
    private void fetchCampaign(String campaignId) {
        final ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("campaigns");
        Query campaignsQuery = ref.orderByKey().equalTo(campaignId);
        campaignsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot campaignSnapshot : dataSnapshot.getChildren()) {
                    Campaign tempCampaign = campaignSnapshot.getValue(Campaign.class);
                    campaigns.add(tempCampaign);
                }

                Log.i("tst", "tst");
                // todo: strike actual price
                // todo: Show only integer
                tvPrice.setText(String.valueOf( Math.round(campaigns.get(0).getPrice() - (campaigns.get(0).getPrice() * campaigns.get(0).getBaseDiscount() / 100))));
                tvActualPrice.setText(String.valueOf(campaigns.get(0).getPrice()));
                tvProductName.setText(String.valueOf(campaigns.get(0).getName()));
                tvSubProductName.setText(String.valueOf(campaigns.get(0).getName()));
                tvTotalQuantity.setText(String.valueOf(campaigns.get(0).getTotalQuantity()));
                baseDiscount.setText(String.valueOf(campaigns.get(0).getBaseDiscount()));
                maxDiscount.setText(String.valueOf(campaigns.get(0).getMaxDiscount()));
                seekbar.setMax(campaigns.get(0).getTotalQuantity());
                seekbar.setProgress(campaigns.get(0).getAvailableQuantity());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == ivBack) {
            if (isBuyerHomeActivity) {
                ((BuyerHomeActivity) activity).onBackPressed();
            } else {
                ((SellerHomeActivity) activity).onBackPressed();
            }
        } else if (view == btnCreate) {
            if (isBuyerHomeActivity) {
                if (isBuyerHomeActivity) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).contains(StringConstants.PREF_USERNAME))
                        createOrder(new Order());
                    else {
                        Intent i = new Intent(activity, SignInActivity.class);
                        i.putExtra("role", "buyer");
                        startActivityForResult(i, 201);
                    }
                }
            } else {
                //((SellerHomeActivity) activity).onBackPressed();
            }

        }
    }

    private void createOrder(Order mOrder) {
        //This should be input to this method
        //*******START******
        mOrder = new Order();
        //fetch User key & Campaign key and persist below for future quick access
        mOrder.setUserId(PreferenceManager.getDefaultSharedPreferences(activity).getString(StringConstants.PREF_USERUSERID,""));
        mOrder.setCampaignId(getArguments().getString("bundleOrderId"));
        //while creating order, both total & available quantity will be the same
        mOrder.setOrderQuantity(Integer.parseInt(etQuantity.getText().toString()));
        mOrder.setPrice(Integer.parseInt(tvPrice.getText().toString()));
        //*******END******

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        //generate key and place it as unique id
        String mKey = mDatabase.push().getKey();
        mOrder.setId(mKey);
        mDatabase.child(mKey).setValue(mOrder, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null) {
                    Log.e(TAG, "Failed to write message", databaseError.toException());
                } else {
                    // ToDo: cloud code - do the following
                    reference.getKey();
                    Snackbar.make(etQuantity,"Order placed",Snackbar.LENGTH_LONG).show();


                    if (isBuyerHomeActivity) {
                        ((BuyerHomeActivity) activity).onBackPressed();
                    } else {
                        ((SellerHomeActivity) activity).onBackPressed();
                    }

                    // put Orderid on Campaign: ordersByBuyers (fetch the row from Campaign with specific key & update new orderId on that
                    // put Orderid on User: ordersByBuyers
                    // update CAMPAIGN row: available stock (fetch current available stock & reduce the orderedQuantity from it
                }
            }

        });


    }

}
