package com.tiger.jump.high.sharecamera;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by yb on 16-4-10.
 */
public class BaseActivity extends Activity {

    protected Toast toast;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    protected void showToast(String msg) {
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
