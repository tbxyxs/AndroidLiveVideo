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
import com.android.tolin.app.live.filter.BeautyFilter;
import com.android.tolin.app.live.filter.CameraPrviewFilter;
import com.android.tolin.app.live.filter.GroupFilter;
import com.android.tolin.app.live.filter.LivePrviewFilter;
import com.android.tolin.app.live.filter.NoFilter;
import com.android.tolin.app.live.filter.TextureFilter;
import com.android.tolin.app.live.utils.Gl2Utils;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 */
public class LiveRenderer extends AbsGLRenderer {

    private final GroupFilter groupFilter;
    private final BeautyFilter beautyFilter;
    private NoFilter noFilter;
    //    private LivePrviewFilter liveFilter;
    private Context context;
    private int textureId;
    private float[] matrix = new float[16];
    private WeakReference<? extends SurfaceTexture> surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    //    private AbsFilter mOesFilter;
    private String cameraId = "0";

    public LiveRenderer(Context context, PSurfaceTexture surfaceTexture, String cameraId) {
        this.context = context;
        this.surfaceTexture = new WeakReference<>(surfaceTexture);
        this.textureId = surfaceTexture.getTextureId();
        this.cameraId = cameraId;
        Resources resources = context.getResources();
//        this.liveFilter = new LivePrviewFilter(resources);
        noFilter = new NoFilter(resources);
        beautyFilter = new BeautyFilter(resources);
        groupFilter = new GroupFilter(resources);
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
        groupFilter.setSize(width,height);
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
        noFilter.setMatrix(matrix);
        groupFilter.setMatrix(matrix);
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
        noFilter.create();
        noFilter.setTextureId(textureId);
        groupFilter.create();
        groupFilter.setTextureId(textureId);
        groupFilter.addFilter(beautyFilter);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setPreviewViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (getSurfaceTexture() != null) {
            getSurfaceTexture().updateTexImage();
//            //获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
//            getSurfaceTexture().getTransformMatrix(matrix);

        }
        noFilter.draw();
        groupFilter.setTextureId(textureId);
        groupFilter.draw();
    }

    @Override
    public void addFilter(AbsFilter filter) {
        groupFilter.addFilter(filter);
    }

    public void destroy() {
        noFilter.destroy();
        groupFilter.destroy();
    }


}
