package com.ecommerce.seller.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ecommerce.R;
import com.ecommerce.data.Campaign;
import com.ecommerce.seller.SellerHomeActivity;
import com.rapidbizapps.android.pickAPic.OnGetOriginalBitmap;
import com.rapidbizapps.android.pickAPic.OnImageSelect;
import com.rapidbizapps.android.pickAPic.PikAPic;

import java.io.File;


public class SellerAddProductFragment extends Fragment implements View.OnClickListener, OnImageSelect, OnGetOriginalBitmap {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://ecom-3ce95.appspot.com");    //change the url according to your firebase app

    private TextView tvStartDate, tvEndDate;
    private EditText etName, etPrice, etTotalUnits, etBaseDiscount, etmaxDiscount;
    private Button btnCreate;
    private Activity activity;
    private View view;
    private PikAPic pikAPic;
    private ImageView ivBack, ivProduct;
    private String TAG = "SellerAddProductFragment.java";
    private String filePath = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public SellerAddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_seller_add_product, container, false);
        initControls();
        return view;
    }

    private void initControls() {
        tvStartDate = (TextView) view.findViewById(R.id.tv_startDate_add_product);
        tvEndDate = (TextView) view.findViewById(R.id.tv_endDate_add_product);
        etName = (EditText) view.findViewById(R.id.et_name_add_product);
        etPrice = (EditText) view.findViewById(R.id.et_price_name_add_product);
        etTotalUnits = (EditText) view.findViewById(R.id.et_total_units_add_product);
        etBaseDiscount = (EditText) view.findViewById(R.id.et_baseDiscount_add_product);
        etmaxDiscount = (EditText) view.findViewById(R.id.et_maxDiscount_add_product);
        btnCreate = (Button) view.findViewById(R.id.btn_create_add_product);
        ivBack = (ImageView) view.findViewById(R.id.iv_back_add_product);
        ivProduct = (ImageView) view.findViewById(R.id.iv_product_add_product);

        ivBack.setOnClickListener(this);
        ivProduct.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        pikAPic = new PikAPic.Builder(activity)
                .setCallback(this)
                .setDialogBackgroundColor("#000")
                .setDialogTitleTextColor("#fff")
                .setDialogButtonTextColor("#49F227")
                .setSavingDirectoryName("hackthon")
                .setDialogTitle("Pick Pic")
                .setImageDimensions(500, 500)
                .build();
        //// TODO: 9/7/2017 have to add datepicker and Imagepicker 

    }

    @Override
    public void onClick(View view) {
        if (view == btnCreate) {
            //// TODO: 9/7/2017 create order
            if (etName.getText().toString().isEmpty() || etPrice.getText().toString().isEmpty() || etmaxDiscount.getText().toString().isEmpty() ||
                    etBaseDiscount.getText().toString().isEmpty() || etTotalUnits.getText().toString().isEmpty())
                /*Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();*/
                Snackbar.make(view, "all are mandatory fields", Snackbar.LENGTH_LONG).show();

            else if (filePath.length() > 0) uploadFile();
            else createCampaign("");
        } else if (view == ivBack) {
            ((SellerHomeActivity) activity).onBackPressed();
        } else if (view == ivProduct) {
            pikAPic.pickImage();
        }
    }

    @Override
    public void onImageSelectionSuccess(String filePath, Bitmap imgBitmap) {
        Log.d(TAG, "imagePickerResult: File PAth : " + filePath + ", Bitmap : " + imgBitmap);
        this.filePath = filePath;
        //filePath_tv.setText(filePath);
        ivProduct.setImageBitmap(imgBitmap);
    }

    @Override
    public void onSelectionError(String err) {
        Log.d(TAG, "imagePickerError: " + err);
        Toast.makeText(activity, "Error msg : " + err, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveOriginal(Bitmap imgBitmap) {
        Log.d(TAG, "recievedOriginalBitmap: Bitmap : " + imgBitmap);
        ivProduct.setImageBitmap(imgBitmap);
    }

    @Override
    public void onOriginalError(String err) {
        Log.d(TAG, "originalBitmapError: " + err);
        Toast.makeText(activity, "Error msg : " + err, Toast.LENGTH_SHORT).show();
    }

    private void createCampaign(String url) {

        //This should be input to this method
        //*******START******
        Campaign mCampaign = new Campaign();
        mCampaign.setActive(true);
        //while creating order, both total & available quantity will be the same
        mCampaign.setTotalQuantity(Integer.parseInt(etTotalUnits.getText().toString()));
        mCampaign.setAvailableQuantity(Integer.parseInt(etTotalUnits.getText().toString()));
        mCampaign.setBaseDiscount(Integer.parseInt(etBaseDiscount.getText().toString()));
        //todo: valid Date in string format
        mCampaign.setMaxDiscount(Integer.parseInt(etmaxDiscount.getText().toString()));
        //fetch this image URL from uploadFile function
        mCampaign.setImageURL(url);
        mCampaign.setName(etName.getText().toString());
        mCampaign.setPrice(Integer.parseInt(etPrice.getText().toString()));
        //*******END******

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("campaigns");
        //generate key and place it as unique id
        String mKey = mDatabase.push().getKey();
        mCampaign.setId(mKey);
        mDatabase.child(mKey).setValue(mCampaign);
        Snackbar.make(view, "Campaign successfully created", Snackbar.LENGTH_LONG).show();
        ((SellerHomeActivity) activity).onBackPressed();
    }


    private void uploadFile() {
        //todo: Image picker
        Uri file = Uri.fromFile(new File(filePath));
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());

// Register observers to listen for when the download is done or if it fails
        riversRef.putFile(file).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                createCampaign(downloadUrl.toString());

                //todo: store this link on Campaign
            }
        });
    }

}
