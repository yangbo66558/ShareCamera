package com.tiger.jump.high.sharecamera.takevideo;

/**
 * Created by yb on 16-4-10.
 */
public class RecordingSize {
    public int width;
    public int height;

    public RecordingSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "RecordingSize{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
