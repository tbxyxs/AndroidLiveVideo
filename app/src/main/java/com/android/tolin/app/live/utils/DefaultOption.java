package com.android.tolin.app.live.utils;

import android.graphics.Point;

public class DefaultOption implements ICamera.IOption {
    private float rate = 1.778f;

    @Override
    public Point getPreSize() {
        return new Point(720, 720);
    }

    @Override
    public Point getPicSize() {
        return new Point(720, 720);
    }

    public float getRate() {
        return rate;
    }
}
