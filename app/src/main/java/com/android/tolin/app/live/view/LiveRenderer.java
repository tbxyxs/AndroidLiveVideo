/*
 *
 * CameraDrawer.java
 *
 * Created by Wuwang on 2016/11/5
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.android.tolin.app.live.camera.AbsGLRenderer;
import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.filter.GroupFilter;
import com.android.tolin.app.live.filter.NoFilter;
import com.android.tolin.app.live.filter.TextureFilter;
import com.android.tolin.app.live.utils.Gl2Utils;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 */
public class LiveRenderer extends AbsGLRenderer {

    private Context context;
    private TextureFilter mEffectFilter;
    private NoFilter mShowFilter;
    private int textureId;
    private float[] matrix = new float[16];
    private WeakReference<? extends SurfaceTexture> surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    //    private AbsFilter mOesFilter;
    private String cameraId = "0";
    private GroupFilter mGroupFilter;                           //中间特效
    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    public LiveRenderer(Context context, PSurfaceTexture surfaceTexture, String cameraId) {
        this.context = context;
        this.surfaceTexture = new WeakReference<>(surfaceTexture);
        this.textureId = surfaceTexture.getTextureId();
        this.cameraId = cameraId;
        Resources resources = context.getResources();
//        this.mOesFilter = new CameraPrviewFilter(resources);
        this.mEffectFilter = new TextureFilter(resources);
        this.mShowFilter = new NoFilter(resources);
        this.mGroupFilter = new GroupFilter(resources);
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
//        mOesFilter.setMatrix(matrix);
        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(matrix);
        mGroupFilter.setSize(dataWidth, dataHeight);
        mEffectFilter.setSize(dataWidth, dataHeight);
        mShowFilter.setSize(dataWidth, dataHeight);
        mGroupFilter.setSize(dataWidth, dataHeight);
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
        mEffectFilter.create();
        mShowFilter.create();
        mGroupFilter.create();
//        mOesFilter.setTextureId(textureId);
        mEffectFilter.setTextureId(textureId);
        mShowFilter.setTextureId(textureId);
        mGroupFilter.setTextureId(textureId);
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
//        mOesFilter.draw();
        mEffectFilter.draw();
        mGroupFilter.setTextureId(mEffectFilter.getOutputTexture());
        mGroupFilter.draw();

        //显示传入的texture上，一般是显示在屏幕上
        GLES30.glViewport(0, 0, width, height);
        mShowFilter.setMatrix(matrix);
        mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
        mShowFilter.draw();

    }

    @Override
    public void addFilter(AbsFilter filter) {
        mGroupFilter.addFilter(filter);
    }

    public void destroy() {
//        mOesFilter.destroy();
        mEffectFilter.destroy();
        mShowFilter.destroy();
        mGroupFilter.destroy();
    }


}
