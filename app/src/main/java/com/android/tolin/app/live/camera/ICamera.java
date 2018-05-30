package com.android.tolin.app.live.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

import com.android.tolin.app.live.view.AbsGLSurfaceView;

public interface ICamera<T> {
    void setPreviewTexture(SurfaceTexture surfaceTexture);

    void startPreview();

    void stopPreview();

    void pause();

    void resume();

    void destory();

    boolean switchTo(int cameraId);

    void switchCamera();

    String getCurrCameraId();

    int cameraCount();

    boolean isPreview();


    /**
     * 获取预览视图大小。
     *
     * @return
     */
    Size getPreviewSize();

    /**
     * 计算匹配相机预览大小 。
     */
    Size computerPreviewSize(AbsGLSurfaceView surfaceView);


    interface TakePhotoCallback {
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

}
