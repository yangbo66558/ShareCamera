package com.tiger.jump.high.sharecamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tiger.jump.high.sharecamera.tools.Lg;

import java.util.List;

/**
 * Created by yb on 16-3-28.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw
        // the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            Lg.d("Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your
        // activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events
        // here.
        // Make sure to stop the preview before resizing or reformatting it.

        Lg.v("surfaceChanged format:" + format + ", w:" + w + ", h:" + h);
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, w, h);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);

            double targetRatio = (double) w / h;
            sizes = parameters.getSupportedPictureSizes();
            optimalSize = getOptimalPictureSize(sizes, targetRatio);
            parameters.setPictureSize(optimalSize.width, optimalSize.height);
            parameters.setRotation(0);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Lg.e(e.toString());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Lg.e("Error starting camera preview: " + e.getMessage());
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        Lg.i("width: " + optimalSize.width + ", height: " + optimalSize.height + ", minDiff: " + minDiff);
        return optimalSize;
    }

    private Camera.Size getOptimalPictureSize(List<Camera.Size> sizes, double targetRatio) {

        final double ASPECT_TOLERANCE = 0.05;

        if (sizes == null)
            return null;

        Lg.i("targetRatio: " + targetRatio);
        Camera.Size optimalSize = null;
        int optimalSideLen = 0;
        double optimalDiffRatio = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {

            int sideLen = Math.max(size.width, size.height);
            //LogEx.i("size.width: " + size.width + ", size.height: " + size.height);
            boolean select = false;
            if (sideLen < IMedia.kPhotoMaxSaveSideLen) {
                if (0 == optimalSideLen || sideLen > optimalSideLen) {
                    select = true;
                }
            } else {
                if (IMedia.kPhotoMaxSaveSideLen > optimalSideLen) {
                    select = true;
                } else {
                    double diffRatio = Math.abs((double) size.width / size.height - targetRatio);
                    if (diffRatio + ASPECT_TOLERANCE < optimalDiffRatio) {
                        select = true;
                    } else if (diffRatio < optimalDiffRatio + ASPECT_TOLERANCE && sideLen < optimalSideLen) {
                        select = true;
                    }
                }
            }

            if (select) {
                optimalSize = size;
                optimalSideLen = sideLen;
                optimalDiffRatio = Math.abs((double) size.width / size.height - targetRatio);
            }
        }

        Lg.i("width: " + optimalSize.width + ", height: " + optimalSize.height + ", optimalDiffRatio: " + optimalDiffRatio);
        return optimalSize;
    }

}
