/*
 *
 * NoFilter.java
 *
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;


/**
 * 无效果的filter，用于显示原始图像效果。
 */
public class NoFilter extends AbsFilter {

    public NoFilter(Resources res) {
        super(res);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/live_no_filter_base_vertex.glsl",
                "shader/live_no_filter_base_fragment.glsl");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

    @Override
    protected void onBindTexture() {
        //生成一个纹理
        //将此纹理绑定到外部纹理上
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        //设置纹理过滤参数
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void onDraw() {
        super.onDraw();
    }
}
