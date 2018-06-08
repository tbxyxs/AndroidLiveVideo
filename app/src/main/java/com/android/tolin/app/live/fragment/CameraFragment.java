package com.android.tolin.app.live.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.android.tolin.app.live.camera.Runnable;
import com.android.tolin.app.live.presenter.CameraPresenter;
import com.android.tolin.app.live.view.CameraGLSurfaceView;
import com.android.tolin.app.live.view.Constant;

public class CameraFragment extends Fragment implements ICameraFragment {
    private static final String TAG = CameraFragment.class.getSimpleName();
    private CameraGLSurfaceView glvCamera;
    private Runnable run;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_fragment_camera_preview, container, false);
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

        view.findViewById(R.id.acbSwitch)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CameraPresenter cameraPresenter = glvCamera.getPresenter();
                        boolean flag = cameraPresenter.isUsing();
                        if (flag) {
                            return;
                        }
                        cameraPresenter.switchCamera();
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Constant.CAMERA_ACCESS_REQUEST_CODE == requestCode) {
            //      Log.v(TAG, "onRequestPermissionsResult-->permissions.length=" + permissions.length);
            glvCamera.post(run);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    Constant.CAMERA_ACCESS_REQUEST_CODE);
            //    Log.v(TAG, "checkCameraPermission");
        }
    }

}
