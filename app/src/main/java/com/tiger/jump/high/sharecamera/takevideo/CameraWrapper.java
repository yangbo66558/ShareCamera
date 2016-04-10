package com.tiger.jump.high.sharecamera.takevideo;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;

import com.tiger.jump.high.sharecamera.tools.Lg;

import java.io.IOException;
import java.util.List;

/**
 * Created by yb on 16-4-10.
 */
public class CameraWrapper {

    private static final String TAG = CameraWrapper.class.getCanonicalName() + ":";

    private Camera mCamera = null;
    private Camera.Parameters mParameters = null;

    public void openCamera() throws OpenCameraException {
        mCamera = null;
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new OpenCameraException(OpenCameraException.OpenType.INUSE);
        }
        if (mCamera == null) throw new OpenCameraException(OpenCameraException.OpenType.NOCAMERA);
    }

    public void stopPreview() throws Exception {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
    }

    public void configureForPreview(int viewWidth, int viewHeight) {
        final Camera.Parameters params = mCamera.getParameters();
        final Camera.Size previewSize = getOptimalSize(params.getSupportedPreviewSizes(), viewWidth, viewHeight);
        params.setPreviewSize(previewSize.width, previewSize.height);
        params.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(params);
        Lg.d("Preview size: " + previewSize.width + "x" + previewSize.height);
    }

    public void enableAutoFocus() {
        final Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(params);
    }

    public void startPreview(final SurfaceHolder holder) throws IOException {
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
    }

    //================

    /**
     * Copyright (C) 2013 The Android Open Source Project
     * <p>
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p>
     * http://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    public Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        final double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        final int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (final Camera.Size size : sizes) {
            final double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (final Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    //=====================

    public void prepareCameraForRecording() throws PrepareCameraException {
        try {
            mParameters = mCamera.getParameters();
            mCamera.unlock();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new PrepareCameraException();
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public RecordingSize getSupportedRecordingSize(int width, int height) {
        Camera.Size recordingSize = getOptimalSize(getSupportedVideoSizes(), width, height);
        if (recordingSize == null) {
            Lg.d("Failed to find supported recording size - falling back to requested: " + width + "x" + height);
            return new RecordingSize(width, height);
        }
        Lg.d("Recording size: " + recordingSize.width + "x" + recordingSize.height);
        return new RecordingSize(recordingSize.width, recordingSize.height);
    }

    private List<Camera.Size> getSupportedVideoSizes() {
        Camera.Parameters params = mParameters;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return params.getSupportedVideoSizes();
        } else {
            Lg.d("Using supportedPreviewSizes iso supportedVideoSizes due to API restriction");
            return params.getSupportedPreviewSizes();
        }
    }

    public void releaseCamera() {
        if (getCamera() == null) return;
        mCamera.release();
    }

}
