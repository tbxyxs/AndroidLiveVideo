package com.android.tolin.app.live.activityes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.fragment.LiveFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flMain, new LiveFragment(), "camera_fragment")
                .commit();

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.flMain, new TestGLFragment(), "camera_fragment")
//                .commit();
    }

}
