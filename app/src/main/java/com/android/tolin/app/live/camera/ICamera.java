package com.android.tolin.app.live.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

import com.android.tolin.app.live.view.AbsGLSurfaceView;

public interface ICamera<T> {

    void startPreview();

    void stopPreview();

    /**
     * 生命周期
     */
    void onPause();

    /**
     * 生命周期
     */
    void onResume();

    void destory();

    boolean switchTo(String cameraId);

    void switchCamera();

    String getCurrCameraId();

    int cameraCount();

    boolean isPreview();


    /**
     * 获取预览数据大小。
     *
     * @return
     */
    Size getCameraPreviewDataSize();

    /**
     * 计算匹配相机预览大小 。
     */
    Size computerPreviewSize(AbsGLSurfaceView surfaceView);

    /**
     * 设置帧率
     *
     * @param fps
     */
    void setCameraFps(int fps);

    /**
     * 计算视频与视图的匹配尺寸比例
     *
     * @param surfaceView
     * @return
     */
    Size computerVideoSize(AbsGLSurfaceView surfaceView);

    /**
     * 获取录制视频大小
     *
     * @return
     */
    Size getCameraVideoSize();

    T getCamera();

    interface TakePhotoCallback {
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

}
