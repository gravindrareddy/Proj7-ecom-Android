package com.ecommerce.seller.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ecommerce.R;
import com.ecommerce.data.Campaign;
import com.ecommerce.seller.SellerHomeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerProductListFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rv_product;
    private Activity activity;
    private View view;
    private List<Campaign> mList;
    private ProductListAdapter adapter;
    private FloatingActionButton fab;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public SellerProductListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_seller_product_list, container, false);
        initControls();
        return view;
    }

    private void initControls() {
        rv_product = (RecyclerView) view.findViewById(R.id.rv_productList_seller);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        rv_product.setLayoutManager(new LinearLayoutManager(activity));
        mList = new ArrayList<>();
        //// TODO: 9/7/2017 getList of ptoducts of seller instead of dummy list

    }

    public void fetchCampaigns() {
        mList.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("campaigns");
        //todo sort by created date
        Query campaignsQuery = ref.orderByKey();
        campaignsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot campaignSnapshot : dataSnapshot.getChildren()) {
                    Campaign tempCampaign = campaignSnapshot.getValue(Campaign.class);
                    mList.add(tempCampaign);
                }
                adapter = new ProductListAdapter(mList, activity);
                rv_product.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCampaigns();
    }
    /* private void getDummyData() {
        Product product = new Product();
        product.setPrice("6000");
        product.setName("Moto e");
        product.setMax(100);
        product.setProgress(50);
        product.setBaseDiscount("10");
        product.setTotalDiscount("15");
        product.setTotalUnits("100");
        mList.add(product);

        product = new Product();
        product.setPrice("8000");
        product.setName("Lonovo");
        product.setMax(100);
        product.setProgress(80);
        product.setBaseDiscount("10");
        product.setTotalDiscount("15");
        product.setTotalUnits("1000");
        mList.add(product);

        product = new Product();
        product.setPrice("50000");
        product.setName("Mac Pro");
        product.setMax(100);
        product.setProgress(20);
        product.setBaseDiscount("10");
        product.setTotalDiscount("15");
        product.setTotalUnits("50");
        mList.add(product);

        product = new Product();
        product.setPrice("700");
        product.setName("Trimmer");
        product.setMax(100);
        product.setProgress(25);
        product.setBaseDiscount("10");
        product.setTotalDiscount("15");
        product.setTotalUnits("250");
        mList.add(product);
    }*/

    @Override
    public void onClick(View view) {
        if (view == fab) {
            ((SellerHomeActivity) activity).addFragment(new SellerAddProductFragment());
        }
    }


    private class ProductListAdapter extends RecyclerView.Adapter<Holder> {
        private List<Campaign> mProductList;
        private Context mContext;

        private ProductListAdapter(List<Campaign> mProductList, Context mContext) {
            this.mProductList = mProductList;
            this.mContext = mContext;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_for_seller_product_list, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            final Campaign product = mProductList.get(position);
            holder.llOnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerProductDetailsFragment spf = new SellerProductDetailsFragment();
                    Bundle b = new Bundle();
                    b.putString("bundleOrderId",mList.get(mList.indexOf(product)).getId());
                    spf.setArguments(b);
                    ((SellerHomeActivity) mContext).addFragment(spf);
                }
            });
            holder.tvName.setText(product.getName());
            holder.tvPrice.setText("\u20B9" + product.getPrice());
            holder.tvBaseDiscount.setText(product.getBaseDiscount() + "%");
            holder.tvTotalDiscount.setText(product.getMaxDiscount() + "%");
            holder.tvTotalTotalUnits.setText(product.getTotalQuantity() + "  units");
            holder.seekBar.setMax(product.getTotalQuantity());
            holder.seekBar.setProgress((product.getTotalQuantity() - product.getAvailableQuantity() / product.getTotalQuantity() * 100));
            if (product.getImageURL().length() > 0)
                Picasso.with(mContext).load(product.getImageURL()).placeholder(R.drawable.phone).into(holder.ivItem);

        }

        @Override
        public int getItemCount() {
            return mProductList.size();
        }
    }


    private class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPrice, tvBaseDiscount, tvTotalDiscount, tvTotalTotalUnits;
        private SeekBar seekBar;
        private ImageView ivItem;
        private LinearLayout llOnClick;

        public Holder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_seller_productList);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_cost_seller_productList);
            tvBaseDiscount = (TextView) itemView.findViewById(R.id.tv_base_discount_seller_productList);
            tvTotalDiscount = (TextView) itemView.findViewById(R.id.tv_total_discount_seller_productList);
            tvTotalTotalUnits = (TextView) itemView.findViewById(R.id.tv_total_units_seller_productList);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekBar_seller_productList);
            ivItem = (ImageView) itemView.findViewById(R.id.iv_product_seller_productList);
            llOnClick = (LinearLayout) itemView.findViewById(R.id.ll_onclick_seller_productList);
        }
    }

    private class Product {
        String name, price, baseDiscount, totalDiscount, totalUnits, img;
        int progress, max;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getBaseDiscount() {
            return baseDiscount;
        }

        public void setBaseDiscount(String baseDiscount) {
            this.baseDiscount = baseDiscount;
        }

        public String getTotalDiscount() {
            return totalDiscount;
        }

        public void setTotalDiscount(String totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public String getTotalUnits() {
            return totalUnits;
        }

        public void setTotalUnits(String totalUnits) {
            this.totalUnits = totalUnits;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
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
