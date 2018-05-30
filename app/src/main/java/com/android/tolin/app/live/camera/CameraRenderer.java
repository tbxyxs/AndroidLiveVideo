/*
 *
 * CameraDrawer.java
 *
 * Created by Wuwang on 2016/11/5
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.camera;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;

import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.filter.OesFilter;
import com.android.tolin.app.live.utils.Gl2Utils;
import com.android.tolin.app.live.view.PSurfaceTexture;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class CameraRenderer extends AbsGLRenderer {

    private int textureId;
    private float[] matrix = new float[16];
    private WeakReference<? extends SurfaceTexture> surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    private AbsFilter mOesFilter;
    private String cameraId = "0";

    public CameraRenderer(Resources res, PSurfaceTexture surfaceTexture, String cameraId) {
        this.mOesFilter = new OesFilter(res);
        this.surfaceTexture = new WeakReference<>(surfaceTexture);
        this.textureId = surfaceTexture.getTextureId();
        this.cameraId = cameraId;
    }

    public void setDataSize(int dataWidth, int dataHeight) {
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        calculateMatrix();
    }

    public void setViewSize(int width, int height) {
        this.width = width;
        this.height = height;
        calculateMatrix();
    }

    private void calculateMatrix() {
        Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
        if ("1".equals(cameraId)) {
            Gl2Utils.flip(matrix, true, false);
            Gl2Utils.rotate(matrix, 90);
        } else {
            Gl2Utils.rotate(matrix, 270);
        }
        mOesFilter.setMatrix(matrix);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture.get();
    }

    public void setCameraId(String id) {
        this.cameraId = id;
        calculateMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mOesFilter.create();
        mOesFilter.setTextureId(textureId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (surfaceTexture.get() != null) {
            surfaceTexture.get().updateTexImage();
        }
        mOesFilter.draw();

    }

    public void destroy() {
        mOesFilter.destroy();
    }

}
