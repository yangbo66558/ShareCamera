package com.tiger.jump.high.sharecamera.takevideo;

import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;
import com.tiger.jump.high.sharecamera.takevideo.VideoConfigurationChooser.CaptureQuality;
import com.tiger.jump.high.sharecamera.takevideo.VideoConfigurationChooser.CaptureResolution;

/**
 * Created by yb on 16-4-10.
 */
public class UserConfiguration implements Parcelable {

    private static final String TAG = UserConfiguration.class.getCanonicalName() + ":";

    public static final String EXTRA_USER_CONFIGURATION = "com.tight.jump.high.extra.user.configuration";

    //系数
    private static final int MBYTE_TO_BYTE = 1024 * 1024;
    private static final int SEC_TO_MSEC = 1000;
    public static final int NO_DURATION_LIMIT = -1;
    public static final int NO_FILESIZE_LIMIT = -1;

    //参数（默认）
    private int mVideoWidth = VideoConfigurationChooser.WIDTH_720P;
    private int mVideoHeight = VideoConfigurationChooser.HEIGHT_720P;
    private int mBitrate = VideoConfigurationChooser.BITRATE_HQ_720P;
    private int mMaxDurationMs = NO_DURATION_LIMIT;
    private int mMaxFilesizeBytes = NO_FILESIZE_LIMIT;
    private int OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;
    private int AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC;
    private int VIDEO_SOURCE = MediaRecorder.VideoSource.CAMERA;
    private int VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264;

    public UserConfiguration() {
    }

    public UserConfiguration(CaptureResolution resolution, CaptureQuality quality) {
        mVideoWidth = resolution.width;
        mVideoHeight = resolution.height;
        mBitrate = resolution.getBitrate(quality);
    }

    public UserConfiguration(CaptureResolution resolution, CaptureQuality quality, int maxDurationSecs, int maxFilesizeMb) {
        this(resolution, quality);
        mMaxDurationMs = maxDurationSecs * SEC_TO_MSEC;
        mMaxFilesizeBytes = maxFilesizeMb * MBYTE_TO_BYTE;
    }

    public UserConfiguration(int videoWidth, int videoHeight, int bitrate) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        mBitrate = bitrate;
    }

    public UserConfiguration(int videoWidth, int videoHeight, int bitrate, int maxDurationSecs, int maxFilesizeMb) {
        this(videoWidth, videoHeight, bitrate);
        mMaxDurationMs = maxDurationSecs * SEC_TO_MSEC;
        mMaxFilesizeBytes = maxFilesizeMb * MBYTE_TO_BYTE;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoBitrate() {
        return mBitrate;
    }

    public int getMaxCaptureDuration() {
        return mMaxDurationMs;
    }

    public int getMaxCaptureFileSize() {
        return mMaxFilesizeBytes;
    }

    public int getOutputFormat() {
        return OUTPUT_FORMAT;
    }

    public int getAudioSource() {
        return AUDIO_SOURCE;
    }

    public int getAudioEncoder() {
        return AUDIO_ENCODER;
    }

    public int getVideoSource() {
        return VIDEO_SOURCE;
    }

    public int getVideoEncoder() {
        return VIDEO_ENCODER;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mVideoWidth);
        dest.writeInt(mVideoHeight);
        dest.writeInt(mBitrate);
        dest.writeInt(mMaxDurationMs);
        dest.writeInt(mMaxFilesizeBytes);

        dest.writeInt(OUTPUT_FORMAT);
        dest.writeInt(AUDIO_SOURCE);
        dest.writeInt(AUDIO_ENCODER);
        dest.writeInt(VIDEO_SOURCE);
        dest.writeInt(VIDEO_ENCODER);
    }

    public static final Creator<UserConfiguration> CREATOR = new Creator<UserConfiguration>() {
        @Override
        public UserConfiguration createFromParcel(Parcel in) {
            return new UserConfiguration(in);
        }

        @Override
        public UserConfiguration[] newArray(int size) {
            return new UserConfiguration[size];
        }
    };

    private UserConfiguration(Parcel in) {
        mVideoWidth = in.readInt();
        mVideoHeight = in.readInt();
        mBitrate = in.readInt();
        mMaxDurationMs = in.readInt();
        mMaxFilesizeBytes = in.readInt();

        OUTPUT_FORMAT = in.readInt();
        AUDIO_SOURCE = in.readInt();
        AUDIO_ENCODER = in.readInt();
        VIDEO_SOURCE = in.readInt();
        VIDEO_ENCODER = in.readInt();
    }

    @Override
    public String toString() {
        return "CaptureConfiguration{" +
                "mVideoWidth=" + mVideoWidth +
                ", mVideoHeight=" + mVideoHeight +
                ", mBitrate=" + mBitrate +
                ", mMaxDurationMs=" + mMaxDurationMs +
                ", mMaxFilesizeBytes=" + mMaxFilesizeBytes +
                ", OUTPUT_FORMAT=" + OUTPUT_FORMAT +
                ", AUDIO_SOURCE=" + AUDIO_SOURCE +
                ", AUDIO_ENCODER=" + AUDIO_ENCODER +
                ", VIDEO_SOURCE=" + VIDEO_SOURCE +
                ", VIDEO_ENCODER=" + VIDEO_ENCODER +
                '}';
    }
}
