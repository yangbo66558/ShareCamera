package com.tiger.jump.high.sharecamera;

import android.app.Application;
import android.content.SharedPreferences;

import com.tiger.jump.high.sharecamera.tools.Lg;

/**
 * Created by yb on 16-3-28.
 */
public class MyApp extends Application {

    private static final String kCommonPreference = "common";

    private static MyApp sInstance;

    private SharedPreferences commonPreference;

    @Override
    public void onCreate() {
        super.onCreate();
        Lg.d("Application onCreate");
        sInstance = this;
    }

    public static MyApp instance() {
        return sInstance;
    }

    public SharedPreferences getCommonPreference() {
        if (null == commonPreference)
            commonPreference = getSharedPreferences(kCommonPreference, MODE_PRIVATE);
        return commonPreference;
    }

}
