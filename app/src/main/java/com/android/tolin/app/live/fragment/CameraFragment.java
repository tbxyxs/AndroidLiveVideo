package com.android.tolin.app.live.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_fragment_camera_pre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        glvCamera = view.findViewById(R.id.glvCamera);
        livePresenter = new LivePresenter((LiveGLSurfaceView) glvCamera);
        view.findViewById(R.id.acbSwitch)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        livePresenter.switchCamera();
                    }
                });
    }

    @Override
    public void onResume() {
        glvCamera.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        glvCamera.onPause();
        livePresenter.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        livePresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (TAKE_PHOTO_REQUEST_CODE == requestCode) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
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
