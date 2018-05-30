package com.android.tolin.app.live.activityes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.fragment.CameraFragment;
import com.android.tolin.app.live.fragment.TestGLFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flMain, new CameraFragment(), "camera_fragment")
                .commit();

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.flMain, new TestGLFragment(), "camera_fragment")
//                .commit();
    }

}
