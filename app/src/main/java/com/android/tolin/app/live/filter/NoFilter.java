/*
 *
 * NoFilter.java
 *
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.filter;

import android.content.res.Resources;

/**
 * Description:
 */
public class NoFilter extends AbsFilter {

    public NoFilter(Resources res) {
        super(res);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/live_base_vertex.glsl",
                "shader/live_base_fragment.glsl");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
