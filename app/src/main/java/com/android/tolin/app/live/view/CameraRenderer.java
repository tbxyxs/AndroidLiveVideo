/*
 *
 * CameraDrawer.java
 *
 * Created by Wuwang on 2016/11/5
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.android.tolin.app.live.camera.AbsGLRenderer;
import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.filter.CameraPrviewFilter;
import com.android.tolin.app.live.utils.Gl2Utils;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class CameraRenderer extends AbsGLRenderer {

    private final Context context;
    private int textureId;
    private float[] matrix = new float[16];
    private WeakReference<? extends SurfaceTexture> surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    private AbsFilter mOesFilter;
    private String cameraId = "0";
    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    public CameraRenderer(Context context, PSurfaceTexture surfaceTexture, String cameraId) {
        this.context = context;
        this.surfaceTexture = new WeakReference<>(surfaceTexture);
        this.textureId = surfaceTexture.getTextureId();
        this.cameraId = cameraId;
        this.mOesFilter = new CameraPrviewFilter(context.getApplicationContext().getResources());
    }

    /**
     * 设置camera预览数据分辨率大小
     *
     * @param dataWidth
     * @param dataHeight
     */
    public void setCameraDataSize(int dataWidth, int dataHeight) {
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        calculateMatrix();
    }

    /**
     * 设置预览view控件的实际大小
     *
     * @param width
     * @param height
     */
    public void setPreviewViewSize(int width, int height) {
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
        deleteFrameBuffer();
        GLES30.glGenFramebuffers(1, mExportFrame, 0);
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setPreviewViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (surfaceTexture.get() != null) {
            surfaceTexture.get().updateTexImage();
        }
        mOesFilter.draw();

    }

    @Override
    public void addFilter(AbsFilter filter) {
    }

    public void destroy() {
        mOesFilter.destroy();
    }


}
