package com.android.tolin.app.live.utils;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

public interface ICamera<T> {
    void setPreviewTexture(SurfaceTexture surfaceTexture);

    void startPreview();

    void stopPreview();

    void relese();

    void destory();

    boolean switchTo(int cameraId);

    void switchCamera();

    int getCurrCameraId();

    int cameraCount();

    boolean isPreview();

    IOption getCameraConfig();


//
//    void takePhoto(TakePhotoCallback callback);
//
//    boolean close();
//
//    void setPreviewTexture(SurfaceTexture texture);
//
//    Point getPreviewSize();
//
//    Point getPictureSize();
//
//    void setOnPreviewFrameCallback(PreviewFrameCallback callback);


    /**
     * 相机配置参数属性
     */
    interface IOption {
        /**
         * 获取预览尺寸
         *
         * @return
         */
        Point getPreSize();

        /**
         * 获取图片尺寸
         *
         * @return
         */
        Point getPicSize();


    }


    interface TakePhotoCallback {
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

}
