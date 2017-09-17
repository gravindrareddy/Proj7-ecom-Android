package com.rapidbizapps.android.pickAPic;

import android.graphics.Bitmap;

/**
 * Created by AdityaSinghParmar on 9/29/2016 at 11:49 AM
 */
public interface OnGetOriginalBitmap {

    void onReceiveOriginal(Bitmap imgBitmap);
    void onOriginalError(String err);
}
