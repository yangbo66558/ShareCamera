package com.tiger.jump.high.sharecamera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiger.jump.high.sharecamera.R;

/**
 * Created by yb on 16-4-10.
 */
public class VideoLayout extends FrameLayout implements View.OnClickListener {

    private static final String TAG = VideoLayout.class.getCanonicalName() + ":";

    private ImageView mBtnCancel;
    private ImageView mBtnAccept;
    private SurfaceView mSurfaceView;
    private ImageView mThumbnailView;
    private TextView mBtnRecord;

    private VideoLayoutButtonClickListener btnClickListener;

    public interface VideoLayoutButtonClickListener {
        void onRecordButtonClicked();

        void onAcceptButtonClicked();

        void onCancelButtonClicked();
    }

    public VideoLayout(Context context) {
        super(context);
        initialize(context);
    }

    public VideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        final View framelayout_video = View.inflate(context, R.layout.framelayout_video, this);
        mThumbnailView = (ImageView) framelayout_video.findViewById(R.id.thumbnail_view);
        mSurfaceView = (SurfaceView) framelayout_video.findViewById(R.id.surface_view);
        mBtnAccept = (ImageView) framelayout_video.findViewById(R.id.btn_accept);
        mBtnCancel = (ImageView) framelayout_video.findViewById(R.id.btn_cancel);
        mBtnRecord = (TextView) framelayout_video.findViewById(R.id.btn_record);
        mBtnRecord.setOnClickListener(this);
        mBtnAccept.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    public void setRecordingButtonInterface(VideoLayoutButtonClickListener mBtnInterface) {
        this.btnClickListener = mBtnInterface;
    }

    public SurfaceHolder getPreviewSurfaceHolder() {
        return mSurfaceView.getHolder();
    }

    public void updateUIRecordPrepare() {
        mBtnRecord.setSelected(false);
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnRecord.setBackgroundResource(R.drawable.camera_on);
        mBtnAccept.setVisibility(View.GONE);
        mBtnCancel.setVisibility(View.GONE);
        mThumbnailView.setVisibility(View.GONE);
        mSurfaceView.setVisibility(View.VISIBLE);
    }

    public void updateUIRecording() {
        mBtnRecord.setSelected(true);
        mBtnRecord.setVisibility(View.VISIBLE);
        mBtnRecord.setBackgroundResource(R.drawable.camera_start);
        mBtnAccept.setVisibility(View.GONE);
        mBtnCancel.setVisibility(View.GONE);
        mThumbnailView.setVisibility(View.GONE);
        mSurfaceView.setVisibility(View.VISIBLE);
    }

    public void updateUIRecordingFinished(Bitmap videoThumbnail) {
        mBtnRecord.setVisibility(View.INVISIBLE);
        mBtnAccept.setVisibility(View.VISIBLE);
        mBtnCancel.setVisibility(View.VISIBLE);
        mThumbnailView.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.GONE);
        final Bitmap thumbnail = videoThumbnail;
        if (thumbnail != null) {
            mThumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mThumbnailView.setImageBitmap(videoThumbnail);
        }
    }

    public static final int MODE_BEFORE = 0;
    public static final int MODE_RECORDING = 1;
    public static final int MODE_STOP = 2;

    public void updateUITimeCountDown(int mode) {
        updateUITimeCountDown(mode, 0);
    }

    public void updateUITimeCountDown(int mode, int time) {
        switch (mode) {
            case MODE_STOP:
                mBtnRecord.setText("");
                break;
            case MODE_BEFORE:
            case MODE_RECORDING:
                mBtnRecord.setText("" + time);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (btnClickListener == null) return;

        if (v.getId() == mBtnRecord.getId()) {
            btnClickListener.onRecordButtonClicked();
        } else if (v.getId() == mBtnAccept.getId()) {
            btnClickListener.onAcceptButtonClicked();
        } else if (v.getId() == mBtnCancel.getId()) {
            btnClickListener.onCancelButtonClicked();
        }

    }

    @Override
    public String toString() {
        return "VideoCaptureView{" +
                "mBtnCancel=" + mBtnCancel +
                ", mBtnAccept=" + mBtnAccept +
                ", mBtnRecord=" + mBtnRecord +
                ", mSurfaceView=" + mSurfaceView +
                ", mThumbnailView=" + mThumbnailView +
                ", btnClickListener=" + btnClickListener +
                '}';
    }
}
