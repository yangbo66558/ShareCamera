package com.tiger.jump.high.sharecamera;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tiger.jump.high.sharecamera.takepic.CustomCameraActivity;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniView();
        setView();
    }

    private void setView() {
        btnCapture.setOnClickListener(this);
    }

    private void iniView() {
        btnCapture = (Button) findViewById(R.id.btn_capture);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                CustomCameraActivity.open(this);
                break;
            default:
                break;
        }
    }
}
