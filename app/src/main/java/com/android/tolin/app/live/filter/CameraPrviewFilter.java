/*
 *
 * CameraFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;

import java.util.Arrays;

/**
 * camera预览filter，没有添加任何预览效果
 */
public class CameraPrviewFilter extends AbsFilter {

    private int mHCoordMatrix;
    private float[] mCoordMatrix= Arrays.copyOf(OM,16);

    public CameraPrviewFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/camera/oes_base_vertex.glsl", "shader/camera/oes_base_fragment.glsl");
        mHCoordMatrix= GLES30.glGetUniformLocation(mProgram,"vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix){
        this.mCoordMatrix=matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES30.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
    }

    @Override
    protected void onBindTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0+getTextureType());
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,getTextureId());
        GLES30.glUniform1i(mHTexture,getTextureType());
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

}
