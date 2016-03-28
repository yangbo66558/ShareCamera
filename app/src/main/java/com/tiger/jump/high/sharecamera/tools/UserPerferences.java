package com.tiger.jump.high.sharecamera.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.tiger.jump.high.sharecamera.MyApp;

/**
 * Created by yb on 16-3-28.
 */
public class UserPerferences {
    private static SharedPreferences sPerences;

    public static SharedPreferences perference() {
        if (sPerences == null) {
            sPerences = MyApp.instance().getSharedPreferences("user_", Context.MODE_PRIVATE);
        }
        return sPerences;
    }

    public static void release() {
        sPerences = null;
    }
}
