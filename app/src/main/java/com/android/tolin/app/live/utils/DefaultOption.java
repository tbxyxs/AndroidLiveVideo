package com.android.tolin.app.live.utils;

import android.graphics.Point;

import com.android.tolin.app.live.camera.ICamera;

public class DefaultOption {
    private float rate = 1.778f;

    public Point getPreSize() {
        return new Point(720, 720);
    }

    public Point getPicSize() {
        return new Point(720, 720);
    }

    public float getRate() {
        return rate;
    }
}
