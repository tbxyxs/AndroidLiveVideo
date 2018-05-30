package com.android.tolin.app.live.presenter;

public interface ILive {

    /**
     * 拍照/截图
     */
    void takePic();

    /**
     * 录制视频
     */
    void recVideo();

    void filter();

    /**
     * 切换前后镜头。
     */
    void switchCamera();
}
