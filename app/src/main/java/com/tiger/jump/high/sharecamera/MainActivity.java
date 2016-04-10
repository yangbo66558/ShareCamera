package com.tiger.jump.high.sharecamera;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tiger.jump.high.sharecamera.takepic.CustomCameraActivity;
import com.tiger.jump.high.sharecamera.takevideo.CustomVideoActivity;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Button btnCapture;
    private Button btn_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniView();
        setView();
    }

    private void setView() {
        btnCapture.setOnClickListener(this);
        btn_video.setOnClickListener(this);
    }

    private void iniView() {
        btnCapture = (Button) findViewById(R.id.btn_capture);
        btn_video = (Button) findViewById(R.id.btn_video);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                CustomCameraActivity.open(this);
                break;
            case R.id.btn_video:
                CustomVideoActivity.open(this);
                break;
            default:
                break;
        }
    }
}
