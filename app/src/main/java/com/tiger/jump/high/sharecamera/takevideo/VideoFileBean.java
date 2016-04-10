package com.tiger.jump.high.sharecamera.takevideo;

import com.tiger.jump.high.sharecamera.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yb on 16-4-10.
 */
public class VideoFileBean {
    private static final String TAG = VideoFileBean.class.getCanonicalName() + ":";

    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    private static final String DEFAULT_PREFIX = "video_";
    private static final String DEFAULT_EXTENSION = ".mp4";

    public static final String EXTRA_USER_OUTPUT_FILENAME = "com.tiger.jump.high.extra.user.output.filename";
    public static final String SAVED_USER_OUTPUT_FILENAME = "com.tight.jump.high.saved.user.configuration";

    private final String mFilename;
    private Date mDate;

    public VideoFileBean(String filename) {
        this.mFilename = filename;
    }

    public VideoFileBean(String filename, Date date) {
        this(filename);
        this.mDate = date;
    }

    public String getFullPath() {
        return getFile().getAbsolutePath();
    }

    public File getFile() {
        final String filename = generateFilename();
        if (filename.contains(DIRECTORY_SEPARATOR)) return new File(filename);

        final File path = FileUtils.getVideoFile();
        path.mkdirs();
        return new File(path, generateFilename());
    }

    private String generateFilename() {
        if (isValidFilename()) return mFilename;

        final String dateStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(getDate());
        return DEFAULT_PREFIX + dateStamp + DEFAULT_EXTENSION;
    }

    private boolean isValidFilename() {
        if (mFilename == null) return false;
        if (mFilename.isEmpty()) return false;

        return true;
    }

    private Date getDate() {
        if (mDate == null) {
            mDate = new Date();
        }
        return mDate;
    }

    @Override
    public String toString() {
        return "VideoFileBean{" +
                "mFilename='" + mFilename + '\'' +
                ", mDate=" + mDate +
                '}';
    }
}
