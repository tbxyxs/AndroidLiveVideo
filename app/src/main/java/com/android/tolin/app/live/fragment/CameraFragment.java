package com.android.tolin.app.live.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tolin.app.live.R;
import com.android.tolin.app.live.presenter.LivePresenter;
import com.android.tolin.app.live.view.CameraGLSurfaceView;
import com.android.tolin.app.live.view.LiveGLSurfaceView;

public class CameraFragment extends Fragment implements ICameraFragment {
    private static final int TAKE_PHOTO_REQUEST_CODE = 900;
    private GLSurfaceView glvCamera;
    private LivePresenter livePresenter;
    private Runnable run;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_fragment_camera_pre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        run = new Runnable() {
            @Override
            public void run() {
                initView(view);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        } else {
            initView(view);
        }

    }

    private void initView(@NonNull View view) {
        glvCamera = view.findViewById(R.id.glvCamera);
        livePresenter = new LivePresenter((LiveGLSurfaceView) glvCamera);
        view.findViewById(R.id.acbSwitch)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = livePresenter.isUsing();
                        if (flag) {
                            return;
                        }
                        livePresenter.switchCamera();
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (glvCamera != null) {
            glvCamera.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (glvCamera != null) {
            glvCamera.onPause();
        }
        if (livePresenter != null) {
            livePresenter.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (livePresenter != null) {
            livePresenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (TAKE_PHOTO_REQUEST_CODE == requestCode) {
            glvCamera.post(run);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    TAKE_PHOTO_REQUEST_CODE);
        }
    }

    @Override
    public CameraGLSurfaceView getGLSurfaceView() {
        return null;
    }
}
