package com.android.tolin.app.live.camera;

import com.android.tolin.app.live.camera.ICamera;

public interface ICHelper<T extends ICamera> {


    /**
     * 是否双摄像头
     *
     * @return true：是
     */
    boolean isDouble();

    /**
     * 结束释放资源。
     */
    void destroy();

    /**
     * 是否有闪光灯
     *
     * @return true：有
     */
    boolean isLight();


    void startPreview();

    void stopPreview();

    void relese();

    /**
     * 切换镜头
     */
    void toggle();


    /**
     * 0:后摄像头  1:前摄像头
     *
     * @return
     */
    String getCameraId();

    /**
     * 获取摄像头个数。
     *
     * @return
     */
    int getCameraCount();

    T getCamera();


}
