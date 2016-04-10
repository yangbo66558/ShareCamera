package com.tiger.jump.high.sharecamera.takevideo;

import com.tiger.jump.high.sharecamera.tools.Lg;

/**
 * Created by yb on 16-4-10.
 */
public class PrepareCameraException extends Exception {

    private static final String TAG = PrepareCameraException.class.getCanonicalName() + ":";

    private static final String LOG_PREFIX = "Unable to unlock camera - ";
    private static final String MESSAGE = "Unable to use camera for recording";

    private static final long serialVersionUID = 6305923762266448674L;

    @Override
    public String getMessage() {
        Lg.d(LOG_PREFIX + MESSAGE);
        return MESSAGE;
    }

}
