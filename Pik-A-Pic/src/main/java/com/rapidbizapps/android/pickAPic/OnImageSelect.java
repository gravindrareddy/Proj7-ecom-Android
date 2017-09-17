package com.rapidbizapps.android.pickAPic;

import android.graphics.Bitmap;

/**
 * Created by AdityaSinghParmar on 9/27/2016 at 3:51 PM
 */
public interface OnImageSelect {

    void onImageSelectionSuccess(String filePath, Bitmap imgBitmap);
    void onSelectionError(String err);
}
