package com.tiger.jump.high.sharecamera.takevideo;

import android.view.SurfaceHolder;

import com.tiger.jump.high.sharecamera.tools.Lg;

import java.io.IOException;

/**
 * Created by yb on 16-4-10.
 */
public class CameraSurfacePreview implements SurfaceHolder.Callback {

    private static final String TAG = CameraSurfacePreview.class.getCanonicalName() + ":";

    private final CameraSurfacePreviewListener mCameraSurfacePreviewListener;
    public final CameraWrapper mCameraWrapper;
    private boolean mPreviewRunning = false;

    public interface CameraSurfacePreviewListener {
        void onCameraSurfacePreviewFailed();
    }

    public CameraSurfacePreview(
            CameraSurfacePreviewListener cameraSurfacePreviewListener, CameraWrapper cameraWrapper, SurfaceHolder holder) {
        mCameraSurfacePreviewListener = cameraSurfacePreviewListener;
        mCameraWrapper = cameraWrapper;
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mPreviewRunning) {
            try {
                mCameraWrapper.stopPreview();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        try {
            mCameraWrapper.configureForPreview(width, height);
            Lg.d("Configured camera for preview in surface of " + width + " by " + height);
        } catch (final RuntimeException e) {
            e.printStackTrace();
            Lg.d("Failed to show preview - invalid parameters set to camera preview");
            mCameraSurfacePreviewListener.onCameraSurfacePreviewFailed();
            return;
        }

        try {
            mCameraWrapper.enableAutoFocus();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            Lg.d("AutoFocus not available for preview");
        }

        try {
            mCameraWrapper.startPreview(holder);
            mPreviewRunning = true;
        } catch (final IOException e) {
            e.printStackTrace();
            Lg.d("Failed to show preview - unable to connect camera to preview (IOException)");
            mCameraSurfacePreviewListener.onCameraSurfacePreviewFailed();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            Lg.d("Failed to show preview - unable to start camera preview (RuntimeException)");
            mCameraSurfacePreviewListener.onCameraSurfacePreviewFailed();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public String toString() {
        return "CameraSurfacePreview{" +
                "mCameraSurfacePreviewListener=" + mCameraSurfacePreviewListener +
                ", mCameraWrapper=" + mCameraWrapper +
                ", mPreviewRunning=" + mPreviewRunning +
                '}';
    }
}
