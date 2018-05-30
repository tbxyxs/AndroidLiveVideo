package com.android.tolin.app.live.camera;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Build;

public class Size {
    protected int width;
    protected int height;

    public Size(Size size) {
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size(Camera.Size size) {
        this.width = size.width;
        this.height = size.height;
    }

    /**
     * 该方法只对api>21以上时使用
     *
     * @param size
     */
    public Size(android.util.Size size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.width = size.getWidth();
            this.height = size.getHeight();
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
