package com.tiger.jump.high.sharecamera.tools;

/**
 * Created by yb on 16-3-28.
 */
public class Lg {

    private static final String TAG = "SHARECAMERA";

    private static final boolean kTagRelease = false;

    public static void v(String msg) {
        if (kTagRelease) {
            return;
        }
        android.util.Log.v(TAG + tag(), msg);
    }

    public static void d(String msg) {
        if (kTagRelease) {
            return;
        }
        android.util.Log.d(TAG + tag(), msg);
    }

    public static void i(String msg) {
        if (kTagRelease) {
            return;
        }
        android.util.Log.i(TAG + tag(), msg);
    }

    public static void w(String msg) {
        if (kTagRelease) {
            return;
        }
        android.util.Log.w(TAG + tag(), msg);
    }

    public static void e(String msg) {
        if (kTagRelease) {
            return;
        }
        android.util.Log.e(TAG + tag(), msg);
    }

    private static String tag() {
        String tag = "null_tag";
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (null != stackTrace && stackTrace.length > 2) {
            StackTraceElement ste = stackTrace[2];
            String className = ste.getClassName();
            int index = className.lastIndexOf('.');
            if (index > 0) {
                className = className.substring(index + 1);
            }
            tag = className + "." + ste.getMethodName();
        }
        return tag;
    }

}
