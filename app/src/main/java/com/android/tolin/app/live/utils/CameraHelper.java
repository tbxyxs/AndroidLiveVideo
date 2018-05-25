package com.android.tolin.app.live.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.tolin.app.live.camera.Camera1;
import com.android.tolin.app.live.camera.CameraRenderer;
import com.android.tolin.app.live.view.AbsGLSurfaceView;
import com.android.tolin.app.live.view.PSurfaceTexture;

public class CameraHelper<T extends ICamera> implements ICHelper<T> {
    private PSurfaceTexture surfaceTexture;
    private Context appContext;
    private T mCamera;
    private int cameraId = 0;


    /**
     * 预览的尺寸
     *
     * @param surfaceTexture
     */

    public CameraHelper(Context context, int cameraId, PSurfaceTexture surfaceTexture) {
        this.cameraId = cameraId;
        this.surfaceTexture = surfaceTexture;
        this.appContext = context.getApplicationContext();
        initCamera();
    }


    private void initCamera() {
        if (Build.VERSION.SDK_INT < 21) {//5.0以下
            initVerLess21Camera();
        } else {  // >=5.0系统
            initVerGreater21Camera();
        }
    }

    /**
     * 版本>=21时使用camera2新版api
     */
    private void initVerGreater21Camera() {

    }

    /**
     * 版本小于21的使用旧版camera api
     */
    private void initVerLess21Camera() {
        mCamera = (T) new Camera1(cameraId);
        mCamera.setPreviewTexture(surfaceTexture);
    }

    @Override
    public boolean isDouble() {
        return mCamera.cameraCount() > 1;
    }

    @Override
    public void destroy() {
        mCamera.destory();
        mCamera = null;
        surfaceTexture = null;
        appContext = null;
    }

    @Override
    public boolean isLight() {
        return CameraUtil.checkCameraHardware(appContext);
    }

    @Override
    public void startPreview() {
        mCamera.startPreview();
    }

    @Override
    public void stopPreview() {
        mCamera.stopPreview();
    }

    @Override
    public void relese() {
        mCamera.destory();
        surfaceTexture = null;
        appContext=null;
    }

    @Override
    public void toggle() {
        mCamera.switchCamera();
    }

    @Override
    public void onPause() {
        mCamera.stopPreview();
    }

    @Override
    public void onResume() {
        mCamera.startPreview();
    }

    @Override
    public int getCameraId() {
        return mCamera.getCurrCameraId();
    }

    @Override
    public int getCameraCount() {
        return mCamera.cameraCount();
    }

    @Override
    public T getCamera() {
        return mCamera;
    }

}
