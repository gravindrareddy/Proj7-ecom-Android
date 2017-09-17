package com.ecommerce;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Administrator on 9/7/2017.
 */

public class HackApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/AovelCool.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
