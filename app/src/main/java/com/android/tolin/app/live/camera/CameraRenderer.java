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
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.filter.OesFilter;
import com.android.tolin.app.live.utils.Gl2Utils;
import com.android.tolin.app.live.view.AbsGLSurfaceView;
import com.android.tolin.app.live.view.PSurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class CameraRenderer implements GLSurfaceView.Renderer {

    private int textureId;
    private float[] matrix = new float[16];
    private SurfaceTexture surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    private AbsFilter mOesFilter;
    private int cameraId = 0;

    public CameraRenderer(Resources res, PSurfaceTexture surfaceTexture, int cameraId) {
        mOesFilter = new OesFilter(res);
        this.surfaceTexture = surfaceTexture;
        textureId = surfaceTexture.getTextureId();
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
        if (cameraId == 1) {
            Gl2Utils.flip(matrix, true, false);
            Gl2Utils.rotate(matrix, 90);
        } else {
            Gl2Utils.rotate(matrix, 270);
        }
        mOesFilter.setMatrix(matrix);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setCameraId(int id) {
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

        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        mOesFilter.draw();

    }

    public void destroy() {
        mOesFilter.destroy();
        surfaceTexture=null;
    }

}
