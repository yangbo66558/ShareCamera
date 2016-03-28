package com.tiger.jump.high.sharecamera.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.Surface;

/**
 * Created by yb on 16-3-28.
 */
public class Utils {

    public static Bitmap decodeByteArrayWithOptions(byte[] data, BitmapFactory.Options opts) {
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (Throwable e) {
            Lg.e(e.toString());
        }
        return null;
    }

    public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen, boolean recycle) {
        if (null == b || degrees == 0 && b.getWidth() <= maxSideLen + 10 && b.getHeight() <= maxSideLen + 10) {
            return b;
        }
        Matrix m = new Matrix();
        if (degrees != 0) {
            m.setRotate(degrees);
        }
        float scale = Math.min(maxSideLen / b.getWidth(), maxSideLen / b.getHeight());
        if (scale < 1) {
            m.postScale(scale, scale);
        }
        Lg.i("degrees: " + degrees + ", scale: " + scale);
        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (null != b2 && b != b2) {
                if (recycle) {
                    if (null != b && !b.isRecycled()) {
                        b.recycle();
                        b = null;
                    }
                }
                b = b2;
            }
        } catch (OutOfMemoryError e) {
        }
        return b;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
