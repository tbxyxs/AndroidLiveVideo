package com.android.tolin.app.live.presenter;

import com.android.tolin.app.live.filter.AbsFilter;

public interface ILive {

    /**
     * 拍照/截图
     */
    void takePic();

    /**
     * 录制视频
     */
    void startRecVideo();

    /**
     * 停止录制
     */
    void stopRecVideo();

    /**
     * @return true:视频录制中
     */
    boolean isRecordingVideo();

    /**
     * 添加滤镜
     */
    void addFilter(AbsFilter filter);

    /**
     * 切换前后镜头。
     */
    void switchCamera();
}
