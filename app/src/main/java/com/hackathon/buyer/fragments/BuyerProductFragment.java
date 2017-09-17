package com.hackathon.buyer.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.R;
import com.hackathon.SignInActivity;
import com.hackathon.buyer.BuyerHomeActivity;
import com.hackathon.data.Campaign;
import com.hackathon.data.User;
import com.hackathon.seller.SellerHomeActivity;
import com.hackathon.seller.fragments.SellerProductDetailsFragment;
import com.hackathon.utils.StringConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuyerProductFragment extends Fragment {
    private static final String TAG = "BuyerProductFragment";
    private RecyclerView rvProduct;
    private View view;
    private Activity activity;
    private List<Campaign> mProdList = new ArrayList<>();

    public BuyerProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_buyer_product, container, false);
        initToolBar();
        initControls();
        return view;
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolBar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        ((BuyerHomeActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         ((BuyerHomeActivity)getActivity()).getMenuInflater().inflate(R.menu.menu, menu);
         return true;
     }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_seller:
                if (PreferenceManager.getDefaultSharedPreferences(activity).contains(StringConstants.PREF_USERNAME))
                    startActivity(new Intent(activity, SellerHomeActivity.class));
                else {
                    new AlertDialog.Builder(activity)
                            .setMessage("Do you want to register as seller?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    Intent i = new Intent(activity, SignInActivity.class);
                                    i.putExtra("role", "seller");
                                    startActivityForResult(i, 201);
                                    // Do something useful withe the position of the selected radio button
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();

                            // Do something useful withe the position of the selected radio button
                        }
                    }).show();



                }
                return true;
            case R.id.action_wallet:
                ((BuyerHomeActivity) activity).addFragment(new WalletFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initControls() {
        rvProduct = (RecyclerView) view.findViewById(R.id.rv_productList_buyer);
        GridLayoutManager manager = new GridLayoutManager(activity, 2);
        rvProduct.setLayoutManager(manager);
        //// TODO: 9/7/2017 getList of ptoducts instead of dummy list

        // getDummyData();
        fetchCampaigns();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PreferenceManager.getDefaultSharedPreferences(activity).contains(StringConstants.PREF_USERNAME))
            startActivity(new Intent(activity, SellerHomeActivity.class));
    }

    private void fetchCampaigns() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("campaigns");
        //todo sort by created date
        Query campaignsQuery = ref.orderByKey();
        campaignsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot campaignSnapshot : dataSnapshot.getChildren()) {
                    Campaign tempCampaign = campaignSnapshot.getValue(Campaign.class);
                    mProdList.add(tempCampaign);
                }
                BuyerAdapter adapter = new BuyerAdapter(mProdList, activity);
                rvProduct.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
   /* private void getDummyData() {
        BuyerModel buyer = new BuyerModel();
        buyer.setProductName("Moto e");
        buyer.setPrice("9000");
        buyer.setMax(100);
        buyer.setProgress(10);
        mProdList.add(buyer);

        buyer = new BuyerModel();
        buyer.setProductName("lenovo e");
        buyer.setPrice("8000");
        buyer.setMax(100);
        buyer.setProgress(80);
        mProdList.add(buyer);


        buyer = new BuyerModel();
        buyer.setProductName("Mac Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);

        buyer = new BuyerModel();
        buyer.setProductName("hero Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);
        buyer = new BuyerModel();
        buyer.setProductName("hero Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);
        buyer = new BuyerModel();
        buyer.setProductName("hero Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);
        buyer = new BuyerModel();
        buyer.setProductName("hero Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);
        buyer = new BuyerModel();
        buyer.setProductName("hero Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);
        buyer = new BuyerModel();
        buyer.setProductName("hero Pro");
        buyer.setPrice("80000");
        buyer.setMax(100);
        buyer.setProgress(70);
        mProdList.add(buyer);
    }*/

    private class BuyerAdapter extends RecyclerView.Adapter<Holder> {
        private List<Campaign> mList;
        private Context mContext;

        private BuyerAdapter(List<Campaign> mList, Context mContext) {
            this.mList = mList;
            this.mContext = mContext;

        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_for_buyer_grid, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final Campaign model = mList.get(position);
            holder.tvName.setText(model.getName());
            holder.tvPrice.setText("Rs." + model.getPrice());
            holder.seekbar.setMax(model.getTotalQuantity());
            holder.seekbar.setProgress(model.getAvailableQuantity());
            if (model.getImageURL().length() > 0)
                Picasso.with(mContext).load(model.getImageURL()).placeholder(R.drawable.phone).into(holder.ivImg);
            holder.rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerProductDetailsFragment spf = new SellerProductDetailsFragment();
                    Bundle b = new Bundle();
                    b.putString("bundleOrderId", model.getId());
                    spf.setArguments(b);
                    ((BuyerHomeActivity) mContext).addFragment(spf);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPrice;
        private ImageView ivImg;
        private SeekBar seekbar;
        private RelativeLayout rlClick;

        public Holder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_productName_buyer);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price_buyer);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_product_buyer);
            seekbar = (SeekBar) itemView.findViewById(R.id.seekBar_buyer);
            rlClick = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_onclick);
        }
    }

    private class BuyerModel {
        private String productName, price, imgUrl;
        private int progress, max;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

}
