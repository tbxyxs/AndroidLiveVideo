package com.android.tolin.app.live.utils;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

/**
 * 5.0及以上使用的camera为
 */
public class Camera2<T extends Camera2> implements ICamera {

    @Override
    public void setPreviewTexture(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void relese() {

    }

    @Override
    public void destory() {

    }

    @Override
    public boolean switchTo(int cameraId) {
        return false;
    }

    @Override
    public void switchCamera() {

    }

    @Override
    public int getCurrCameraId() {
        return 0;
    }

    @Override
    public int cameraCount() {
        return 0;
    }

    @Override
    public boolean isPreview() {
        return false;
    }

    @Override
    public IOption getCameraConfig() {
        return null;
    }
}
