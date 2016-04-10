package com.tiger.jump.high.sharecamera.takevideo;

import android.media.MediaRecorder;
import android.view.SurfaceHolder;

import com.tiger.jump.high.sharecamera.tools.Lg;

import java.io.IOException;

/**
 * Created by yb on 16-4-10.
 */
public class VideoRecorderController implements CameraSurfacePreview.CameraSurfacePreviewListener, MediaRecorder.OnInfoListener {
    private CameraWrapper mCameraWrapper;
    private final UserConfiguration mUserConfiguration;
    private final VideoFileBean mVideoFileBean;
    private final VideoRecorderControllerListener mControllerListener;
    private CameraSurfacePreview mCameraSurfacePreview;
    private boolean mIsRecording = false;
    private MediaRecorder mRecorder;

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
        mControllerListener.onRecordingFailed("无法录制视频，请重试");
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        switch (what) {
            case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
                break;
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                Lg.d("MediaRecorder max duration reached");
                stopRecording(null/*"Capture stopped - Max duration reached"*/);
                break;
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                Lg.d("MediaRecorder max filesize reached");
                stopRecording(null/*"Capture stopped - Max file size reached"*/);
                break;
            default:
                break;
        }
    }

    //==========

    public void toggleRecording() {
        if (mIsRecording) {
            stopRecording(null);
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        mIsRecording = false;

        if (!initRecorder()) return;
        if (!prepareRecorder()) return;
        if (!startRecorder()) return;

        mIsRecording = true;
        mControllerListener.onRecordingStarted();
        Lg.d("Successfully started recording - outputfile: " + mVideoFileBean.getFullPath());
    }

    private boolean initRecorder() {
        try {
            mCameraWrapper.prepareCameraForRecording();
        } catch (final PrepareCameraException e) {
            e.printStackTrace();
            mControllerListener.onRecordingFailed("无法录制视频，请重试");
            Lg.d("Failed to initialize recorder - " + e.toString());
            return false;
        }

        mRecorder = new MediaRecorder();
        mRecorder.setCamera(mCameraWrapper.getCamera());
        mRecorder.setAudioSource(mUserConfiguration.getAudioSource());
        mRecorder.setVideoSource(mUserConfiguration.getVideoSource());
        RecordingSize size = mCameraWrapper.getSupportedRecordingSize(mUserConfiguration.getVideoWidth(), mUserConfiguration.getVideoHeight());
        mRecorder.setOutputFormat(mUserConfiguration.getOutputFormat());
        mRecorder.setVideoSize(size.width, size.height);
        mRecorder.setVideoEncodingBitRate(mUserConfiguration.getVideoBitrate());
        mRecorder.setAudioEncoder(mUserConfiguration.getAudioEncoder());
        mRecorder.setVideoEncoder(mUserConfiguration.getVideoEncoder());
        mRecorder.setMaxDuration(mUserConfiguration.getMaxCaptureDuration());
        mRecorder.setOutputFile(mVideoFileBean.getFullPath());
        try {
            mRecorder.setMaxFileSize(mUserConfiguration.getMaxCaptureFileSize());
        } catch (IllegalArgumentException e) {
            Lg.d("Failed to set max filesize - illegal argument: " + mUserConfiguration.getMaxCaptureFileSize());
        } catch (RuntimeException e2) {
            Lg.d("Failed to set max filesize - runtime exception");
        }
        mRecorder.setOnInfoListener(this);
        Lg.d("MediaRecorder successfully initialized");
        return true;
    }

    private boolean prepareRecorder() {
        try {
            mRecorder.prepare();
            Lg.d("MediaRecorder successfully prepared");
            return true;
        } catch (final IllegalStateException e) {
            e.printStackTrace();
            Lg.d("MediaRecorder preparation failed - " + e.toString());
            return false;
        } catch (final IOException e) {
            e.printStackTrace();
            Lg.d("MediaRecorder preparation failed - " + e.toString());
            return false;
        }
    }

    private boolean startRecorder() {
        try {
            mRecorder.start();
            Lg.d("MediaRecorder successfully started");
            return true;
        } catch (final IllegalStateException e) {
            e.printStackTrace();
            Lg.d("MediaRecorder start failed - " + e.toString());
            return false;
        } catch (final RuntimeException e2) {
            e2.printStackTrace();
            Lg.d("MediaRecorder start failed - " + e2.toString());
            mControllerListener.onRecordingFailed("录制视频的权限被禁止，请去设置中开启");
            return false;
        }
    }

    private void stopRecording(String message) {
        if (!mIsRecording) return;
        try {
            mRecorder.stop();
            mControllerListener.onRecordingSuccess();
            Lg.d("Successfully stopped recording - outputfile: " + mVideoFileBean.getFullPath());
        } catch (final RuntimeException e) {
            Lg.d("Failed to stop recording");
        }
        mIsRecording = false;
        mControllerListener.onRecordingStopped(message);
    }

    public void releaseAllResources() {
        if (mCameraSurfacePreview != null) {
            mCameraSurfacePreview.releasePreviewResources();
        }
        if (mCameraWrapper != null) {
            mCameraWrapper.releaseCamera();
            mCameraWrapper = null;
        }
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        Lg.d("Released all resources");
    }


}
