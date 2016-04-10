package com.tiger.jump.high.sharecamera.takepic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by yb on 16-3-28.
 */
public class CaptureSensorsObserver implements SensorEventListener {

    private RefocuseListener listener;
    private SensorManager mSensorManager;
    private Sensor sensor;
    private boolean waitingFocuse;

    private static final int kMotiveThresholdCount = 2;
    private static final int kStaticThresholdCount = 3;
    private static final int MOVE_THRESHOLD = 20;
    private int staticCount;
    private long mLastUpdate;
    private float mLastX;
    private float mLastY;
    private float mLastZ;

    public void setRefocuseListener(RefocuseListener l) {
        this.listener = l;
    }

    public interface RefocuseListener {
        void needFocuse();
    }

    public CaptureSensorsObserver(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        waitingFocuse = true;
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        mSensorManager.unregisterListener(this, sensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            if (mLastUpdate + 100 > curTime) {
                return;
            }

            long diffTime = (curTime - mLastUpdate);
            mLastUpdate = curTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float dx = x - mLastX;
            float dy = y - mLastY;
            float dz = z - mLastZ;

            float speed = (float) (Math.sqrt(dx * dx + dy * dy + dz * dz)
                    / diffTime * 10000);
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            //LogEx.i("speed: " + speed + ", MOVE_THRESHOLD: " + MOVE_THRESHOLD);
            if (speed > MOVE_THRESHOLD) {
                staticCount = staticCount < 0 ? --staticCount : -1;
                if (staticCount + kMotiveThresholdCount <= 0) {
                    waitingFocuse = true;
                }
            } else {
                staticCount = staticCount > 0 ? ++staticCount : 1;
                if (waitingFocuse && staticCount >= kStaticThresholdCount) {
                    waitingFocuse = false;
                    if (null != listener) {
                        listener.needFocuse();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
