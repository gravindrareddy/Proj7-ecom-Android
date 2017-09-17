package com.rapidbizapps.android.pickAPic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by AdityaSinghParmar on 9/27/2016 at 3:52 PM
 */
public class PikAPic implements GlobalConstants{


    private static final String TAG = "PikAPic";
    private static final String DIALOG_TAG = "MY_DIALOG";


    /**
     * The library now supports compatibility Activity,Fragment as
     * well as normal Activity, Fragment. Here we are using REQUESTER_TYPE
     * flag to define the type of requesting entity.
     */
    private static final int REQUESTER_TYPE_NOT_DEFINED = 0;
    private static final int REQUESTER_TYPE_COMPAT = 1;

    private static final int REQUESTER_TYPE_NOT_COMPAT = 2;

    private int REQUESTER_TYPE = REQUESTER_TYPE_NOT_DEFINED;
    private PikAPicFragmentCompat fragmentCompat;   //  Called if requesting entity is app_compat

    private PikAPicFragment fragment;               //  Called if requesting entity is not compat
    /**
     * Pattern is used to validate Hexadecimal input that we receive as color.
     */
    private static final String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    private static final Pattern colorPattern = Pattern.compile(HEX_PATTERN);


    /**
     * Default colors, used for validating the color.
     */
    private static final String defaultColors[] = {
            String.valueOf(Color.BLACK), String.valueOf(Color.BLUE),
            String.valueOf(Color.CYAN), String.valueOf(Color.DKGRAY),
            String.valueOf(Color.GRAY), String.valueOf(Color.GREEN),
            String.valueOf(Color.LTGRAY), String.valueOf(Color.MAGENTA),
            String.valueOf(Color.RED), String.valueOf(Color.TRANSPARENT),
            String.valueOf(Color.WHITE), String.valueOf(Color.YELLOW)
    };


    private final Object mFragmentOrActivity;           //  requesting entity
    private String dialogTitle = DEFAULT_DIALOG_TITLE;
    private String directoryName = DEFAULT_DIRECTORY_NAME;
    private int dialogBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int dialogTitleTextColor = DEFAULT_DIALOG_TITLE_TEXT_COLOR;
    private int dialogButtonTextColor = DEFAULT_DIALOG_BUTTON_TEXT_COLOR;
    private int scalingValue = DEFAULT_SCALING_VALUE;
    private int imageWidth = DEFAULT_BITMAP_WIDTH;
    private int imageHeight = DEFAULT_BITMAP_HEIGHT;
    private OnImageSelect callback;


    private PikAPic(PikAPic.Builder builder) {

        checkNotNull(builder);
        mFragmentOrActivity = builder.mContext;
        dialogTitle = builder.mDialogTitle;
        directoryName = builder.mDirectoryName;
        dialogBackgroundColor = builder.mDialogBackgroundColor;
        dialogTitleTextColor = builder.mDialogTitleTextColor;
        dialogButtonTextColor = builder.mDialogButtonTextColor;
        scalingValue = builder.mScalingValue;
        imageHeight = builder.mImageHeight;
        imageWidth = builder.mImageWidth;
        callback = builder.callback;

        /*
            Once the dialog properties are received,
            we create the Fragment.
         */
        setupBundleForPickAPicFragment();
    }


    /**
     * Called when we have already set up the object using builder but need a new callback.
     * callback can't be null. If builder is not used, it will set everything as default.
     * @param callback, must be implemented by calling class.
     */
    public void pickImage(@NonNull OnImageSelect callback) {

        checkNotNull(callback);
        this.callback = callback;

        switch (REQUESTER_TYPE) {
            case REQUESTER_TYPE_COMPAT:
                fragmentCompat.onImageSelect = callback;
                break;

            case REQUESTER_TYPE_NOT_COMPAT:
                fragment.onImageSelect = callback;
                break;

            case REQUESTER_TYPE_NOT_DEFINED:
                callback.onSelectionError("Initialization error");
        }

        showSelectionDialog();

    }

    /**
     * Called when we need to pick an image and have already initialized everything.
     * If OnImageSelect is not initialized, we will throw a NullPointerException
     */
    public void pickImage() {

        showSelectionDialog();
    }


    /**
     * Based on the type of requesting entity, calls the createDialog
     * method of fragment
     */
    private void showSelectionDialog() {
        switch (REQUESTER_TYPE) {
            case REQUESTER_TYPE_COMPAT:
                fragmentCompat.createDialog();
                break;

            case REQUESTER_TYPE_NOT_COMPAT:
                fragment.createDialog();
                break;

            case REQUESTER_TYPE_NOT_DEFINED:
                callback.onSelectionError("Initialization error");
        }
    }

    /**
     * If all the details are being sent..
     * @deprecated use the builder class to initialize and then pickImage() instead
     */
    public void pickImage(OnImageSelect onImageSelect, int scalingValue,
                          String dialogTitle, String dialogBackgroundColor,
                          String dialogTitleTextColor, String buttonTextColor){

        this.callback = onImageSelect;
        this.scalingValue = scalingValue;
        this.dialogTitle = dialogTitle;
        this.dialogBackgroundColor = validateAndConvertColorType(dialogBackgroundColor,DEFAULT_BACKGROUND_COLOR);
        this.dialogTitleTextColor = validateAndConvertColorType(dialogTitleTextColor,DEFAULT_DIALOG_TITLE_TEXT_COLOR);
        this.dialogButtonTextColor = validateAndConvertColorType(buttonTextColor, DEFAULT_DIALOG_BUTTON_TEXT_COLOR);

        setupIntentForImageAPI();
    }


    /************************************************* HELPER METHODS ****************************/


    /**
     * Changes String with length 3 to a string with length 6.
     * Don't know how, not fail proof
     * @param givenColor, to be changed
     * @return color, changed
     */
    private static String convert3to6HexString(String givenColor){

        checkNotNull(givenColor);
        givenColor = HASH_SYMBOL
                + givenColor.substring(1,2)+ givenColor.substring(1,2)
                + givenColor.substring(2,3)+ givenColor.substring(2,3)
                + givenColor.substring(3,4)+ givenColor.substring(3,4);
        return givenColor.toUpperCase();
    }

    /**
     * Check if the color is present in our list of default colors.
     * @param givenColor, to be tested.
     * @return true if color present in list, false otherwise.
     */
    private static boolean checkFromDefaultColors(String givenColor){

        try {
            return Arrays.asList(defaultColors).contains(givenColor);
        }
        catch (NullPointerException | ClassCastException e){
            return false;
        }
    }


    /**
     * This is for getting the original Bitmap of the image using the given path..
     */
    public void getFullBitmap(OnGetOriginalBitmap onGetOriginalBitmap, String path){

        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        if(myBitmap != null)
            onGetOriginalBitmap.onReceiveOriginal(myBitmap);
        else
            onGetOriginalBitmap.onOriginalError("Error retrieving bitmap, may be path is invalid !!");
    }


    /**
     * Creates intent for ImageAPI with parameters that we set using builder.
     * After setting up the intent, we call the activity.
     * @deprecated , use fragment approach instead
     */
    private void setupIntentForImageAPI() {

        if (callback == null)
            throw new NullPointerException("OnImageSelected interface is not initialized. Please implement this callback.");

        Intent intent;
        if (mFragmentOrActivity instanceof AppCompatActivity) {

            intent = new Intent((AppCompatActivity)mFragmentOrActivity,ImageAPI.class);
            REQUESTER_TYPE = REQUESTER_TYPE_COMPAT;
        }
        else if(mFragmentOrActivity instanceof Activity) {

            intent = new Intent((Activity) mFragmentOrActivity, ImageAPI.class);
            REQUESTER_TYPE = REQUESTER_TYPE_NOT_COMPAT;
        }
        else {
            throw new ClassCastException("Expected activity or fragment");
        }

        ImageAPI.onImageSelect = callback;

        intent.putExtra(KEY_SCALING_VALUE,scalingValue);
        intent.putExtra(KEY_DIALOG_TITLE,dialogTitle);
        intent.putExtra(KEY_DIALOG_BACKGROUND_COLOUR,dialogBackgroundColor);
        intent.putExtra(KEY_DIALOG_TITLE_TEXT_COLOUR,dialogTitleTextColor);
        intent.putExtra(KEY_DIALOG_BUTTON_TEXT_COLOUR,dialogButtonTextColor);
        intent.putExtra(KEY_DIRECTORY_NAME,directoryName);
        intent.putExtra(KEY_BITMAP_HEIGHT,imageHeight);
        intent.putExtra(KEY_BITMAP_WIDTH,imageWidth);

        Log.d(TAG, "setupIntentForImageAPI: "+"creating activity now.");
        switch (REQUESTER_TYPE) {

            case REQUESTER_TYPE_NOT_COMPAT:
                ((Activity)mFragmentOrActivity).startActivity(intent);
                                                            break;
            case REQUESTER_TYPE_COMPAT :
                ((AppCompatActivity)mFragmentOrActivity).startActivity(intent);
                break;
        }
    }


    /**
     * Called right after initializing the PikAPic object.
     * Sets up a bundle that will be sent to particular fragment.
     */
    private void setupBundleForPickAPicFragment() {

        if (callback == null) {
            throw new NullPointerException("OnImageSelected interface is not initialized. Please implement this callback.");
        }


        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SCALING_VALUE, scalingValue);
        bundle.putString(KEY_DIALOG_TITLE, dialogTitle);
        bundle.putInt(KEY_DIALOG_BACKGROUND_COLOUR, dialogBackgroundColor);
        bundle.putInt(KEY_DIALOG_TITLE_TEXT_COLOUR, dialogTitleTextColor);
        bundle.putInt(KEY_DIALOG_BUTTON_TEXT_COLOUR, dialogButtonTextColor);
        bundle.putString(KEY_DIRECTORY_NAME, directoryName);
        bundle.putInt(KEY_BITMAP_HEIGHT, imageHeight);
        bundle.putInt(KEY_BITMAP_WIDTH, imageWidth);

        fragmentCompat = PikAPicFragmentCompat.newInstance(callback, bundle);
        fragment = PikAPicFragment.newInstance(callback,bundle);


        if (mFragmentOrActivity instanceof AppCompatActivity) {
            ((AppCompatActivity)mFragmentOrActivity).getSupportFragmentManager()
                    .beginTransaction().add(fragmentCompat,DIALOG_TAG).commit();
            REQUESTER_TYPE = REQUESTER_TYPE_COMPAT;
        }
        else if(mFragmentOrActivity instanceof Activity) {
            ((Activity)mFragmentOrActivity).getFragmentManager()
                    .beginTransaction().add(fragment,DIALOG_TAG).commit();
            REQUESTER_TYPE = REQUESTER_TYPE_NOT_COMPAT;
        }
        else if (mFragmentOrActivity instanceof Fragment) {
            ((Fragment)mFragmentOrActivity).getChildFragmentManager()
                    .beginTransaction().add(fragmentCompat,DIALOG_TAG).commit();
            REQUESTER_TYPE = REQUESTER_TYPE_COMPAT;
        }
        else if (mFragmentOrActivity instanceof android.app.Fragment) {
            ((android.app.Fragment)mFragmentOrActivity).getFragmentManager()
                    .beginTransaction().add(fragment,DIALOG_TAG).commit();
            REQUESTER_TYPE = REQUESTER_TYPE_NOT_COMPAT;
        }
        else {
            throw new ClassCastException("Expected activity or fragment");
        }
    }


    /**
     * Takes any string value and returns equivalent integer value for
     * the string, if it exists. returns defaultColor otherwise
     * @param potentialColor, to be converted
     * @param defaultColor, returned if can't convert potential color
     * @return integer equivalent of potential color or default color.
     */
    private static int validateAndConvertColorType(String potentialColor, int defaultColor) {

        // if it is a default color, simply convert to Integer.
        //  handles NullPointerException
        if (checkFromDefaultColors(potentialColor))
            return Integer.parseInt(potentialColor);


        //  if the passed parameter is not in expected format return
        //  default color.
        if (!colorPattern.matcher(potentialColor).matches())
            return defaultColor;


        // convert to size 6 before using it
        if (potentialColor.length() == 4)
            potentialColor = convert3to6HexString(potentialColor);

        return Color.parseColor(potentialColor);
    }

    /**********************************************************************************************/

    /**
     * Builder class to remove large number of parametrized constructors.
     * Use this builder pattern to efficiently initialize class.
     *
     * If some field is not initialized, default value will be set.
     */
    public static final class Builder{

        private Context mContext;
        private String mDialogTitle = DEFAULT_DIALOG_TITLE;
        private String mDirectoryName = DEFAULT_DIRECTORY_NAME;
        private int mDialogBackgroundColor = DEFAULT_BACKGROUND_COLOR;
        private int mDialogTitleTextColor = DEFAULT_DIALOG_TITLE_TEXT_COLOR;
        private int mDialogButtonTextColor = DEFAULT_DIALOG_BUTTON_TEXT_COLOR;
        private int mScalingValue = DEFAULT_SCALING_VALUE;
        private int mImageHeight = DEFAULT_BITMAP_HEIGHT;
        private int mImageWidth = DEFAULT_BITMAP_WIDTH;
        private OnImageSelect callback;

        public Builder(Context context) {

            mContext = context;
            if (context instanceof OnImageSelect) {
                callback = (OnImageSelect) context;
            }
        }

        public Builder setDialogTitle(String title) {
            this.mDialogTitle = title;
            return this;
        }

        public Builder setSavingDirectoryName(String directoryName) {
            this.mDirectoryName = directoryName;
            return this;
        }

        public Builder setDialogBackgroundColor(int color) {
            this.mDialogBackgroundColor = color;
            return this;
        }
        public Builder setDialogBackgroundColor(String color) {
            this.mDialogBackgroundColor = validateAndConvertColorType(color,
                    DEFAULT_BACKGROUND_COLOR);
            return this;
        }

        public Builder setDialogTitleTextColor(int color) {
            this.mDialogTitleTextColor = color;
            return this;
        }

        public Builder setDialogTitleTextColor(String color) {
            this.mDialogTitleTextColor = validateAndConvertColorType(color,
                    DEFAULT_DIALOG_TITLE_TEXT_COLOR);
            return this;
        }

        public Builder setDialogButtonTextColor(int color) {
            this.mDialogButtonTextColor = color;
            return this;
        }


        public Builder setDialogButtonTextColor(String color) {
            this.mDialogButtonTextColor = validateAndConvertColorType(color,
                    DEFAULT_DIALOG_BUTTON_TEXT_COLOR);
            return this;
        }

        public Builder setScalingValue(int scalingValue) {
            this.mScalingValue = scalingValue;
            return this;
        }

        public Builder setImageDimensions(int width, int height) {
            mImageHeight = height;
            mImageWidth = width;
            return this;
        }

        public Builder setCallback(OnImageSelect callback) {
            this.callback = callback;
            return this;
        }

        public PikAPic build() {

            return new PikAPic(this);
        }
    }
}
