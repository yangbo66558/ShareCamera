package com.tiger.jump.high.sharecamera.takevideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.tiger.jump.high.sharecamera.R;
import com.tiger.jump.high.sharecamera.tools.Lg;
import com.tiger.jump.high.sharecamera.ui.VideoLayout;

/**
 * Created by yb on 16-4-10.
 */
public class CustomVideoActivity extends Activity {

    private static final String TAG = CustomVideoActivity.class.getCanonicalName() + ":";
    private static final String SAVED_IS_RECORDED = "com.tiger.jump.high.saved.is.recorded";

    private UserConfiguration mUserConfiguration;//用户配置参数
    private int timeCountDown = 0;//倒计时
    private boolean mIsVideoRecorded = false;//是否在拍摄过程中的记录
    private VideoFileBean mVideoFile = null;
    private VideoLayout video_layout;


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
        video_layout = (VideoLayout) findViewById(R.id.video_layout);
        if (video_layout == null) return;


    }

    private void initializeUserConfiguration(Bundle savedInstanceState) {
        mUserConfiguration = generateUserConfiguration();
        timeCountDown = mUserConfiguration.getMaxCaptureDuration() / 1000;
        mIsVideoRecorded = generateVideoIsRecorded(savedInstanceState);
        mVideoFile = generateOutputFile(savedInstanceState);
        Lg.d("mUserConfiguration : " + mUserConfiguration + " , timeCountDown : " + timeCountDown + " , mIsVideoRecorded : " + mIsVideoRecorded + " , mVideoFile : " + mVideoFile);
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
}
