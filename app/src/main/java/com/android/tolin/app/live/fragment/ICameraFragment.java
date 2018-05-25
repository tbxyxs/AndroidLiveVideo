package com.android.tolin.app.live.fragment;

import com.android.tolin.app.live.view.CameraGLSurfaceView;

public interface ICameraFragment {
    /**
     * 检查相机权限。
     */
    void checkCameraPermission();

    CameraGLSurfaceView getGLSurfaceView();
}
