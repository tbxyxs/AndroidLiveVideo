package com.android.tolin.app.live.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.tolin.app.live.camera.Camera1;
import com.android.tolin.app.live.camera.Camera2;
import com.android.tolin.app.live.camera.ICHelper;
import com.android.tolin.app.live.camera.ICamera;
import com.android.tolin.app.live.camera.Size;
import com.android.tolin.app.live.view.AbsGLSurfaceView;
import com.android.tolin.app.live.view.LiveGLSurfaceView;
import com.android.tolin.app.live.view.PSurfaceTexture;

import java.lang.ref.WeakReference;

public class CameraHelper<T extends ICamera> implements ICHelper<T> {
    private WeakReference<AbsGLSurfaceView> glSurfaceView;
    private WeakReference<PSurfaceTexture> surfaceTexture;
    private Context appContext;
    private T mCamera;
    private String cameraId = "0";


    /**
     * 预览的尺寸
     *
     * @param glSurfaceView
     * @param surfaceTexture
     */

    public CameraHelper(AbsGLSurfaceView glSurfaceView, String cameraId, PSurfaceTexture surfaceTexture) {
        this.glSurfaceView =new WeakReference<>(glSurfaceView);
        this.cameraId = cameraId;
        this.surfaceTexture =new WeakReference<>(surfaceTexture);
        this.appContext = glSurfaceView.getContext().getApplicationContext();
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initVerGreater21Camera() {
        mCamera = (T) new Camera2<>(glSurfaceView.get(), surfaceTexture.get(), cameraId);
    }

    /**
     * 版本小于21的使用旧版camera api
     */
    private void initVerLess21Camera() {
        mCamera = (T) new Camera1(glSurfaceView.get(), surfaceTexture.get(), cameraId);
    }

    @Override
    public boolean isDouble() {
        return mCamera.cameraCount() > 1;
    }

    @Override
    public void destroy() {
        if (mCamera != null) {
            mCamera.destory();
            mCamera = null;
        }
        glSurfaceView = null;
        surfaceTexture = null;
        appContext = null;
    }

    @Override
    public boolean isLight() {
        return CameraUtil.checkCameraHardware(appContext);
    }

    @Override
    public void startPreview() {
        if (!mCamera.isPreview()) {
            mCamera.startPreview();
        }
    }

    @Override
    public void stopPreview() {
        mCamera.stopPreview();
    }

    @Override
    public void relese() {
        mCamera.destory();
        surfaceTexture = null;
        appContext = null;
    }

    @Override
    public void toggle() {
        mCamera.switchCamera();
    }

    @Override
    public String getCameraId() {
        return mCamera.getCurrCameraId();
    }

    @Override
    public int getCameraCount() {
        Log.v("surface", "getCameraCount ：" + (mCamera == null));
        return (mCamera == null) ? 0 : mCamera.cameraCount();
    }

    @Override
    public T getCamera() {
        return mCamera;
    }


}
