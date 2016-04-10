package com.tiger.jump.high.sharecamera.takevideo;

import com.tiger.jump.high.sharecamera.tools.Lg;

/**
 * Created by yb on 16-4-10.
 */
public class OpenCameraException extends Exception {

    private static final String TAG = OpenCameraException.class.getCanonicalName() + ":";

    private static final String LOG_PREFIX = "Unable to open camera - ";
    private static final long serialVersionUID = -7340415176385044242L;

    public enum OpenType {
        INUSE("Camera disabled or in use by other process"), NOCAMERA("Device does not have camera");

        private String mMessage;

        private OpenType(String msg) {
            mMessage = msg;
        }

        public String getMessage() {
            return mMessage;
        }

        @Override
        public String toString() {
            return "OpenType{" +
                    "mMessage='" + mMessage + '\'' +
                    '}';
        }
    }

    private final OpenType mType;

    public OpenCameraException(OpenType type) {
        super(type.getMessage());
        mType = type;
    }

    @Override
    public void printStackTrace() {
        Lg.d(LOG_PREFIX + mType.getMessage());
        super.printStackTrace();
    }

    @Override
    public String toString() {
        return "OpenCameraException{" +
                "mType=" + mType +
                '}';
    }

}
