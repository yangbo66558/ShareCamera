package com.tiger.jump.high.sharecamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.OrientationEventListener;

/**
 * Created by yb on 16-3-28.
 */
public class CaptureOrientationEventListener extends OrientationEventListener {

    private Camera mCamera;
    private int mRotation;
    private int mCurrentCameraId;

    public CaptureOrientationEventListener(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (null == mCamera)
            return;
        if (orientation == ORIENTATION_UNKNOWN)
            return;

        orientation = (orientation + 45) / 90 * 90;
        if (android.os.Build.VERSION.SDK_INT <= 8) {
            mRotation = (90 + orientation) % 360;
            return;
        }

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mRotation = (info.orientation - orientation + 360) % 360;
        } else { // back-facing camera
            mRotation = (info.orientation + orientation) % 360;
        }
    }

    public void start(Camera camera, int currentCameraId) {
        this.mCamera = camera;
        this.mCurrentCameraId = currentCameraId;
        enable();
    }

    public void stop() {
        disable();
    }

    public int getRotation() {
        return mRotation;
    }

}
