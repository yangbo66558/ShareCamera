package com.tiger.jump.high.sharecamera.takepic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tiger.jump.high.sharecamera.IMedia;
import com.tiger.jump.high.sharecamera.MyApp;
import com.tiger.jump.high.sharecamera.R;
import com.tiger.jump.high.sharecamera.tools.Lg;
import com.tiger.jump.high.sharecamera.tools.Utils;

import java.util.List;

/**
 * Created by yb on 16-3-24.
 */
public class CustomCameraActivity extends Activity implements View.OnClickListener, CaptureSensorsObserver.RefocuseListener {

    private CaptureSensorsObserver captureSensorsObserver;
    private CaptureOrientationEventListener orientationEventListener;
    private Camera camera;
    private Camera.PictureCallback pictureCallBack;
    private Camera.AutoFocusCallback focusCallback;
    private CameraPreview preview;
    private int currentCameraId;
    private int frontCameraId;
    private boolean isCapturing;
    private int currentMode;
    private static final String kKeyForCurrentLightMode = "camera_light_mode";
    private static final int[] LIGHT_MODE_RESOURCE = {R.drawable.light_on, R.drawable.light_off, R.drawable.light_auto};
    private static final String[] LIGHT_MODE = {Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_OFF, Camera.Parameters.FLASH_MODE_AUTO};


    private View bnFinish;
    private View bnToggleCamera;
    private View bnToggleLight;
    private View bnCapture;
    private View bnToConfirm;
    private View focuesView;
    private FrameLayout framelayoutPreview;


    public static void open(Context mContext) {
        Intent intent = new Intent(mContext, CustomCameraActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        setObserver();
        initView();
        setView();
        setListener();
        setDevice();
    }

    private void setDevice() {
        if (android.os.Build.VERSION.SDK_INT > 8) {
            int cameraCount = Camera.getNumberOfCameras();
            if (cameraCount < 1) {
                Toast.makeText(this, getString(R.string.no_camera), Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else if (cameraCount == 1) {
                bnToggleCamera.setVisibility(View.INVISIBLE);
            } else {
                bnToggleCamera.setVisibility(View.VISIBLE);
            }
            currentCameraId = 0;
            frontCameraId = findFrontFacingCamera();
            if (-1 == frontCameraId) {
                bnToggleCamera.setVisibility(View.INVISIBLE);
            }

            currentMode = MyApp.instance().getCommonPreference().getInt(kKeyForCurrentLightMode, 1);
            setLightView();
        }
    }

    private void setListener() {
        bnFinish.setOnClickListener(this);
        bnToggleCamera.setOnClickListener(this);
        bnToggleLight.setOnClickListener(this);
        bnCapture.setOnClickListener(this);
        bnToConfirm.setOnClickListener(this);
        captureSensorsObserver.setRefocuseListener(this);
        pictureCallBack = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();
                isCapturing = false;
                setLightOffMode();

                Bitmap bitmap = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inSampleSize = Math.max(options.outWidth / IMedia.kPhotoMaxSaveSideLen, options.outHeight / IMedia.kPhotoMaxSaveSideLen);
                    bitmap = Utils.decodeByteArrayWithOptions(data, options);
                    if (null == bitmap) {
                        options.inSampleSize = Math.max(2, options.inSampleSize * 2);
                        bitmap = Utils.decodeByteArrayWithOptions(data, options);
                    }
                } catch (Throwable e) {
                }

                if (null == bitmap) {
                    Toast.makeText(CustomCameraActivity.this, getString(R.string.save_failure), Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap addBitmap = Utils.rotateAndScale(bitmap, orientationEventListener.getRotation(), IMedia.kPhotoMaxSaveSideLen, false);
                if (bitmap != addBitmap) {
                    if (null != bitmap && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            }
        };

        focusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean successed, Camera camera) {
                focuesView.setVisibility(View.INVISIBLE);
            }
        };

    }

    private void setView() {
        bnToConfirm.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        bnFinish = findViewById(R.id.bnFinish);
        bnToggleCamera = findViewById(R.id.bnToggleCamera);
        bnToggleLight = findViewById(R.id.bnToggleLight);
        bnCapture = findViewById(R.id.bnCapture);
        bnToConfirm = findViewById(R.id.bnToConfirm);
        framelayoutPreview = (FrameLayout) findViewById(R.id.cameraPreview);
        focuesView = findViewById(R.id.viewFocus);
    }

    private void setObserver() {
        captureSensorsObserver = new CaptureSensorsObserver(this);
        orientationEventListener = new CaptureOrientationEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        if (null != captureSensorsObserver) {
            captureSensorsObserver.stop();
        }
        releaseCamera();
        captureSensorsObserver.stop();
        orientationEventListener.stop();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if (null != captureSensorsObserver) {
            captureSensorsObserver.setRefocuseListener(null);
            captureSensorsObserver = null;
        }
        if (null != orientationEventListener) {
            orientationEventListener = null;
        }
        MyApp.instance().getCommonPreference().edit().putInt(kKeyForCurrentLightMode, currentMode).commit();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bnFinish:
                finish();
                break;
            case R.id.bnToggleCamera:
                switchCamera();
                break;
            case R.id.bnToggleLight:
                clickLightView((currentMode + 1) % 3);
                break;
            case R.id.bnCapture:
                bnCaptureClicked();
                break;
            case R.id.bnToConfirm:
                finish();
                break;
        }
    }

    @Override
    public void needFocuse() {
        if (null == camera || isCapturing) {
            return;
        }
        camera.cancelAutoFocus();
        try {
            camera.autoFocus(focusCallback);
        } catch (Exception e) {
            Lg.e(e.toString());
            return;
        }
        if (View.INVISIBLE == focuesView.getVisibility()) {
            focuesView.setVisibility(View.VISIBLE);
            focuesView.getParent().requestTransparentRegion(preview);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    //==========

    private void clickLightView(int mode) {
        currentMode = mode;
        setLightView();
        checkMode();
    }

    private void setLightMode() {
        Camera.Parameters parameters = camera.getParameters();
        if (!LIGHT_MODE[currentMode].equals(parameters.getFlashMode())) {
            parameters.setFlashMode(LIGHT_MODE[currentMode]);
            camera.setParameters(parameters);
        }
    }

    private void setLightOffMode() {
        Camera.Parameters parameters = camera.getParameters();
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
        }
    }

    private void setLightView() {
        bnToggleLight.setBackgroundResource(LIGHT_MODE_RESOURCE[currentMode]);
    }

    private boolean checkLight() {
        List<String> flashModes = camera.getParameters().getSupportedFlashModes();
        if (flashModes == null) {
            bnToggleLight.setVisibility(View.GONE);
        } else {
            bnToggleLight.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void checkMode() {
        List<String> flashModes = camera.getParameters().getSupportedFlashModes();
        if (!flashModes.contains(LIGHT_MODE[currentMode])) {
            Toast.makeText(this, "不支持该闪光灯模式", Toast.LENGTH_LONG).show();
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release(); // release the camera for other applications
            camera = null;
        }

        if (null != preview) {
            framelayoutPreview.removeAllViews();
            preview = null;
        }
    }

    private void openCamera() {
        if (android.os.Build.VERSION.SDK_INT > 8) {
            try {
                camera = Camera.open(currentCameraId);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.open_camera_fail), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Utils.setCameraDisplayOrientation(this, 0, camera);
        } else {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.open_camera_fail), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        Camera.Parameters camParmeters = camera.getParameters();
        List<Camera.Size> sizes = camParmeters.getSupportedPreviewSizes();
        for (Camera.Size size : sizes) {
            Lg.v("w:" + size.width + ",h:" + size.height);
        }

        preview = new CameraPreview(this, camera);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        framelayoutPreview.addView(preview, params);
        captureSensorsObserver.start();
        orientationEventListener.start(camera, currentCameraId);
        if (checkLight()) {
            checkMode();
        }
    }

    private void switchCamera() {
        if (currentCameraId == 0) {
            currentCameraId = frontCameraId;
        } else {
            currentCameraId = 0;
        }
        releaseCamera();
        openCamera();
    }

    private void bnCaptureClicked() {
        if (isCapturing) {
            return;
        }
        isCapturing = true;
        focuesView.setVisibility(View.INVISIBLE);
        try {
            setLightMode();
            camera.takePicture(null, null, pictureCallBack);
        } catch (RuntimeException e) {
            isCapturing = false;
        }
    }

}