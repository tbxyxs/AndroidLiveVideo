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

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * live预览filter，没有添加任何预览效果
 */
public class LivePrviewFilter extends AbsFilter {

    private int mHCoordMatrix;
    private float[] mCoordMatrix = Arrays.copyOf(OM, 16);

    public LivePrviewFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/camera/oes_base_vertex.glsl", "shader/camera/oes_base_fragment.glsl");
        mHCoordMatrix = GLES30.glGetUniformLocation(mProgram, "vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix) {
        this.mCoordMatrix = matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES30.glUniformMatrix4fv(mHCoordMatrix, 1, false, mCoordMatrix, 0);
    }

    @Override
    protected void onBindTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + getTextureType());
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES30.glUniform1i(mHTexture, getTextureType());
    }

    @Override
    protected void onDraw() {

        //激活纹理单元0
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定外部纹理到纹理单元0
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES30.glUniform1i(mHTexture, 0);

        //将纹理矩阵传给片段着色器
        GLES30.glUniformMatrix4fv(mHMatrix, 1, false, getMatrix(), 0);

        //将顶点和纹理坐标传给顶点着色器
        if (mVerBuffer != null) {
            //顶点坐标从位置0开始读取
            mVerBuffer.position(0);
            //使能顶点属性
            GLES30.glEnableVertexAttribArray(mHPosition);
            //顶点坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
            GLES30.glVertexAttribPointer(mHPosition, 2, GLES30.GL_FLOAT, false, 16, mTexBuffer);

            //纹理坐标从位置2开始读取
            mTexBuffer.position(2);
            GLES30.glEnableVertexAttribArray(mHCoord);
            //纹理坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4 个字节）的字节继续读取两个顶点值
            GLES30.glVertexAttribPointer(mHCoord, 2, GLES30.GL_FLOAT, false, 16, mTexBuffer);
        }

        //绘制两个三角形（6个顶点）
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);


//        aPositionLocation = glGetAttribLocation(mShaderProgram, FilterEngine.POSITION_ATTRIBUTE);
//        aTextureCoordLocation = glGetAttribLocation(mShaderProgram, FilterEngine.TEXTURE_COORD_ATTRIBUTE);
//        uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_MATRIX_UNIFORM);
//        uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_SAMPLER_UNIFORM);
//
//        glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
//        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
//        glUniform1i(uTextureSamplerLocation, 0);
//        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);
//
//        if (mDataBuffer != null) {
//            mDataBuffer.position(0);
//            glEnableVertexAttribArray(aPositionLocation);
//            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mDataBuffer);
//
//            mDataBuffer.position(2);
//            glEnableVertexAttribArray(aTextureCoordLocation);
//            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, mDataBuffer);
//        }
//
//        //glDrawElements(GL_TRIANGLE_FAN, 6,GL_UNSIGNED_INT, 0);
//        //glDrawArrays(GL_TRIANGLE_FAN, 0 , 6);
//        glDrawArrays(GL_TRIANGLES, 0, 6);
//        //glDrawArrays(GL_TRIANGLES, 3, 3);
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        long t2 = System.currentTimeMillis();
//        long t = t2 - t1;
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

}
