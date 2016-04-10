package com.tiger.jump.high.sharecamera.takevideo;

import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by yb on 16-4-10.
 */
public class VideoRecorderController implements CameraSurfacePreview.CameraSurfacePreviewListener {
    private CameraWrapper mCameraWrapper;
    private final Surface mSurface;
    private final UserConfiguration mUserConfiguration;
    private final VideoFileBean mVideoFileBean;
    private final VideoRecorderControllerListener mControllerListener;
    private CameraSurfacePreview mCameraSurfacePreview;


    public interface VideoRecorderControllerListener {

        void onRecordingStopped(String message);

        void onRecordingStarted();

        void onRecordingSuccess();

        void onRecordingFailed(String message);

    }

    public VideoRecorderController(
            VideoRecorderControllerListener controllerListener,
            UserConfiguration userConfiguration,
            VideoFileBean videoFileBean,
            CameraWrapper cameraWrapper,
            SurfaceHolder surfaceHolder) {
        mControllerListener = controllerListener;
        mUserConfiguration = userConfiguration;
        mVideoFileBean = videoFileBean;
        mCameraWrapper = cameraWrapper;
        mSurface = surfaceHolder.getSurface();
        if (openCamera()) {
            mCameraSurfacePreview = new CameraSurfacePreview(this, mCameraWrapper, surfaceHolder);
        }
    }

    private boolean openCamera() {
        try {
            mCameraWrapper.openCamera();
        } catch (final OpenCameraException e) {
            e.printStackTrace();
            mControllerListener.onRecordingFailed(e.getMessage());
            return false;
        }
        return true;
    }

    //=====

    @Override
    public void onCameraSurfacePreviewFailed() {

    }

}
