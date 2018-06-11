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
import android.opengl.GLES30;
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
    protected void onClear() {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }


    @Override
    protected void onDraw() {
        super.onDraw();
    }
}
