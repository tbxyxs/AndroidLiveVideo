package com.android.tolin.app.live.camera;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.filter.GroupFilter;

import javax.microedition.khronos.opengles.GL10;

public abstract class AbsGLRenderer implements GLSurfaceView.Renderer {
    /**
     * true:打印日志
     */
    public static boolean LOGTOOGLE = true;
    private static final String TAG = AbsGLRenderer.class.getSimpleName();
    private long frameStartTimes;
    private int frameRatio = 30;//每秒30帧。


    /**
     * 设置帧率
     */
    private final void computeFrameRate() {
        long elaFrameTimes = SystemClock.elapsedRealtime() - frameStartTimes;
        long expeFrameTimes = 1000 / frameRatio;
        long timeToSleepMS = expeFrameTimes - elaFrameTimes;
        if (timeToSleepMS > 0) {
            SystemClock.sleep(timeToSleepMS);
        }
        frameStartTimes = SystemClock.elapsedRealtime();
    }

    /**
     * 设置每秒刷新帧率
     *
     * @param frameRate
     */
    protected void setFrameRate(int frameRate) {
        this.frameRatio = frameRate;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        computeFrameRate();
        logFrameRate();
    }

    int frameCount = 0;
    long startTimesMs;

    private void logFrameRate() {
        if (!LOGTOOGLE) {
            return;
        }
        long elaRealtime = SystemClock.elapsedRealtime();
        double elapsedSeconds = (elaRealtime - startTimesMs) / 1000.0;
        if (elapsedSeconds >= 1.0) {
            Log.v(TAG, frameCount / elapsedSeconds + "fps");
            startTimesMs = SystemClock.elapsedRealtime();
            frameCount = 0;
        }
        frameCount++;
    }

    /**
     * 增加滤镜
     *
     * @param filter 滤镜
     */
    public abstract void addFilter(AbsFilter filter);
}
