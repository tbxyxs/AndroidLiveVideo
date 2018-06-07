package com.android.tolin.app.live.activityes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.fragment.CameraFragment;
import com.android.tolin.app.live.fragment.LiveFragment;
import com.android.tolin.app.live.utils.LiveConstants;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Intent intent = getIntent();
        String str = intent.getStringExtra("type");
        Fragment fragment = null;
        if (LiveConstants.CAMERAPREVIEW.getName().equals(str)) {
            fragment = new CameraFragment();
        } else if (LiveConstants.LIVEPREVIEW.getName().equals(str)) {
            fragment = new LiveFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flMain, fragment, "preview")
                .commit();
    }

}
