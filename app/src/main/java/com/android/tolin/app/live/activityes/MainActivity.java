package com.android.tolin.app.live.activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.fragment.CameraFragment;
import com.android.tolin.app.live.fragment.LiveFragment;
import com.android.tolin.app.live.utils.LiveConstants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void bt1(View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        String type = "";
        switch (view.getId()) {
            case R.id.btCameraPreview:
                type = LiveConstants.CAMERAPREVIEW.getName();
                break;
            case R.id.btLivePreview:
                type = LiveConstants.LIVEPREVIEW.getName();
                break;
        }
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
