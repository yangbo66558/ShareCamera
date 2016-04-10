package com.tiger.jump.high.sharecamera.takevideo;

import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.WindowManager;

import com.tiger.jump.high.sharecamera.BaseActivity;
import com.tiger.jump.high.sharecamera.R;
import com.tiger.jump.high.sharecamera.tools.Lg;
import com.tiger.jump.high.sharecamera.ui.VideoLayout;

/**
 * Created by yb on 16-4-10.
 */
public class CustomVideoActivity extends BaseActivity implements VideoLayout.VideoLayoutButtonClickListener, VideoRecorderController.VideoRecorderControllerListener {

    private static final String TAG = CustomVideoActivity.class.getCanonicalName() + ":";
    private static final String SAVED_IS_RECORDED = "com.tiger.jump.high.saved.is.recorded";

    private UserConfiguration mUserConfiguration;//用户配置参数
    private int mTimeCountDown = 0;//倒计时
    private boolean mIsVideoRecordedFinish = false;//是否在拍摄过程中的记录
    private VideoFileBean mVideoFile = null;
    private VideoLayout mVideoLayout;
    private VideoRecorderController mVideoRecorderController;
    private boolean isOneSecReached = true;
    private Handler mHandler = new Handler();


    public static void open(Context mContext) {
        Intent intent = new Intent(mContext, CustomVideoActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_custom_video);
        initializeUserConfiguration(savedInstanceState);
        mVideoLayout = (VideoLayout) findViewById(R.id.video_layout);
        if (mVideoLayout == null) return;
        init();
    }

    private void initializeUserConfiguration(Bundle savedInstanceState) {
        mUserConfiguration = generateUserConfiguration();
        mTimeCountDown = mUserConfiguration.getMaxCaptureDuration() / 1000;
        mIsVideoRecordedFinish = generateVideoIsRecorded(savedInstanceState);
        mVideoFile = generateOutputFile(savedInstanceState);
        Lg.d("mUserConfiguration : " + mUserConfiguration + " , mTimeCountDown : " + mTimeCountDown + " , mIsVideoRecordedFinish : " + mIsVideoRecordedFinish + " , mVideoFile : " + mVideoFile);
    }

    protected VideoFileBean generateOutputFile(Bundle savedInstanceState) {
        VideoFileBean returnFile = null;
        if (savedInstanceState != null) {
            returnFile = new VideoFileBean(savedInstanceState.getString(VideoFileBean.SAVED_USER_OUTPUT_FILENAME));
        } else {
            returnFile = new VideoFileBean(this.getIntent().getStringExtra(VideoFileBean.EXTRA_USER_OUTPUT_FILENAME));
        }
        return returnFile;
    }

    private boolean generateVideoIsRecorded(Bundle savedInstanceState) {
        if (savedInstanceState == null) return false;
        return savedInstanceState.getBoolean(SAVED_IS_RECORDED, false);
    }

    private UserConfiguration generateUserConfiguration() {
        UserConfiguration userConfiguration = this.getIntent().getParcelableExtra(UserConfiguration.EXTRA_USER_CONFIGURATION);
        if (userConfiguration == null) {
            userConfiguration = new UserConfiguration();
            Lg.d("No user configuration passed - using default user configuration");
        }
        return userConfiguration;
    }

    private void init() {
        mVideoRecorderController = new VideoRecorderController(
                this, mUserConfiguration, mVideoFile, new CameraWrapper(), mVideoLayout.getPreviewSurfaceHolder());
        mVideoLayout.setOnVideoLayoutBtnClickListener(this);
        if (mIsVideoRecordedFinish) {
            mVideoLayout.updateUIRecordingFinished(
                    ThumbnailUtils.createVideoThumbnail(mVideoFile.getFullPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
        } else {
            mVideoLayout.updateUIRecordPrepare();
        }
    }

    //==========
    @Override
    public void onRecordButtonClicked() {
        if (isOneSecReached) {
            isOneSecReached = false;
            mHandler.postDelayed(prepare, 2000);
            Lg.d("onRecordButtonClicked");
            mVideoLayout.updateUITimeCountDown(mVideoLayout.MODE_BEFORE, mTimeCountDown);
            mVideoRecorderController.toggleRecording();
            mHandler.postDelayed(timeCycle, 1000);
        } else {
            showToast("时间过短，请勿频繁点击");
        }
    }

    private Runnable prepare = new Runnable() {
        @Override
        public void run() {
            isOneSecReached = true;
        }
    };

    private Runnable timeCycle = new Runnable() {
        @Override
        public void run() {
            mTimeCountDown--;
            if (mTimeCountDown > 0) {
                mVideoLayout.updateUITimeCountDown(VideoLayout.MODE_RECORDING, mTimeCountDown);
                mHandler.postDelayed(this, 1000);
            } else if (mTimeCountDown == 0) {
                mVideoLayout.updateUITimeCountDown(VideoLayout.MODE_STOP);
            }
        }
    };

    @Override
    public void onAcceptButtonClicked() {
        // TODO
        finish();
    }

    @Override
    public void onCancelButtonClicked() {
        //TODO
        finish();
    }

    //==========

    @Override
    public void onRecordingStopped(String message) {
        Lg.d("onRecordingStopped : message : " + message);
        if (message != null) {
            showToast(message);
        }
        mVideoLayout.updateUIRecordingFinished(ThumbnailUtils.createVideoThumbnail(mVideoFile.getFullPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
        mVideoRecorderController.releaseAllResources();
    }

    @Override
    public void onRecordingStarted() {
        Lg.d("onRecordingStarted");
        mVideoLayout.updateUIRecording();
    }

    @Override
    public void onRecordingSuccess() {
        Lg.d("onRecordingSuccess");
        mIsVideoRecordedFinish = true;
        mVideoLayout.updateUITimeCountDown(VideoLayout.MODE_STOP);
    }

    @Override
    public void onRecordingFailed(String message) {
        Lg.d("onRecordingFailed : message : " + message);
        showToast("Can't capture video: " + message);
        finish();
    }

    //==========


    @Override
    protected void onPause() {
        if (mVideoRecorderController != null) {
            mVideoRecorderController.toggleRecording();
            mVideoRecorderController.releaseAllResources();
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SAVED_IS_RECORDED, mIsVideoRecordedFinish);
        savedInstanceState.putString(VideoFileBean.SAVED_USER_OUTPUT_FILENAME, mVideoFile.getFullPath());
        super.onSaveInstanceState(savedInstanceState);
    }
}
