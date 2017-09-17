package com.rapidbizapps.android.pickAPic;

import android.graphics.Color;

/**
 * Created by Sahil Patel on 3/6/2017.
 */

public interface GlobalConstants {

    int DEFAULT_BITMAP_WIDTH = 200;
    int DEFAULT_BITMAP_HEIGHT = 200;

    String KEY_SCALING_VALUE = "scalingValue";
    String KEY_DIALOG_TITLE = "dialogTitle";
    String KEY_DIALOG_BACKGROUND_COLOUR = "dialogBackgroundColour";
    String KEY_DIALOG_TITLE_TEXT_COLOUR = "dialogTitleTextColour";
    String KEY_DIALOG_BUTTON_TEXT_COLOUR = "dialogButtonTextColour";
    String KEY_BITMAP_HEIGHT = "bitmapHeight";
    String KEY_BITMAP_WIDTH = "bitmapWidth";
    String KEY_DIRECTORY_NAME = "directoryName";

    String FORWARD_SLASH = "/";
    String HASH_SYMBOL = "#";
    String EXTENSION_JPG = ".jpg";
    String EXTENSION_JPEG = ".jpeg";
    String EXTENSION_PNG = ".png";

    int DEFAULT_SCALING_VALUE = 300;
    String DEFAULT_DIALOG_TITLE = "Choose image from:";
    String DEFAULT_DIRECTORY_NAME = "pik-a-pic";
    int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#FFFFFF");
    int DEFAULT_DIALOG_TITLE_TEXT_COLOR = Color.parseColor("#787878");
    int DEFAULT_DIALOG_BUTTON_TEXT_COLOR = Color.parseColor("#0C9B8E");
}
