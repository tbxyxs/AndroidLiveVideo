/*
 *
 * GroupFilter.java
 *
 * Created by Wuwang on 2016/12/14
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.filter;

import android.content.res.Resources;
import android.opengl.GLES30;

import com.android.tolin.app.live.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * 容器filter，用于批量绘制处理多个filter.
 */
public class GroupFilter extends AbsFilter {

    private Queue<AbsFilter> mFilterQueue;
    private List<AbsFilter> mFilters;
    private int width = 0, height = 0;
    //创建离屏buffer
    private int filterNum;
    private int[] mFrameBuffers = null;
    private int[] mFrameBufferTextures = null;
    private int[] fTexture = null;
    private int textureIndex = 0;

    /**
     * @param res
     * @param filterNum 过滤器个数为实际filter数量+1
     */
    public GroupFilter(Resources res, int filterNum) {
        super(res);
        this.filterNum = filterNum;
        mFrameBuffers = new int[filterNum];
        mFrameBufferTextures = new int[filterNum];
        fTexture = new int[filterNum];
        mFilters = new ArrayList<>();
        mFilterQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    protected void initBuffer() {

    }

    public void addFilter(final AbsFilter filter) {
        //绘制到frameBuffer上和绘制到屏幕上的纹理坐标是不一样的
        //Android屏幕相对GL世界的纹理Y轴翻转
        MatrixUtils.flip(filter.getMatrix(), false, true);
        mFilterQueue.add(filter);
        mFilters.add(filter);
    }


    public void draw() {
        updateFilter();
        textureIndex = 0;
        if (filterNum > 0) {
            for (AbsFilter filter : mFilters) {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[0]);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, fTexture[textureIndex % 2], 0);
                GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, mFrameBufferTextures[0]);
                GLES30.glViewport(0, 0, width, height);
                if (textureIndex == 0) {
                    filter.setTextureId(getTextureId());
                } else {
                    filter.setTextureId(fTexture[(textureIndex - 1) % 2]);
                }
                filter.draw();
                textureIndex++;
            }
        }

    }

    private void updateFilter() {
        AbsFilter f;
        while ((f = mFilterQueue.poll()) != null) {
            f.create();
            f.setSize(width, height);
            mFilters.add(f);
        }
    }

    @Override
    public int getOutputTexture() {
        return getTextureId();
    }

    @Override
    protected void onCreate() {
        updateFilter();
        createFrameBuffer();
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
        updateFilter();
        createFrameBuffer();
    }


    //创建FrameBuffer
    private void createFrameBuffer() {
        for (int i = 0; i < filterNum - 1; i++) {
            GLES30.glGenFramebuffers(1, mFrameBuffers, i);

            GLES30.glGenTextures(1, mFrameBufferTextures, i);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mFrameBufferTextures[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                    GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[i]);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                    GLES30.GL_TEXTURE_2D, getTextureId(), 0);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        }
    }

    @Override
    public void onDraw() {
        for (int i = 0; i < filterNum; i++) {
            AbsFilter filter = mFilters.get(i);
            boolean isNotLast = i < filterNum - 1;
            if (isNotLast) {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[i]);
                GLES30.glClearColor(0, 0, 0, 0);
            }

            if (i == 0) {
                filter.onDraw();
            } else if (i == filterNum - 1) {
                filter.onDraw();
            } else {
                filter.onDraw();
            }
            if (isNotLast) {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            }
        }
    }

    //生成Textures
    private void genTextures() {
        GLES30.glGenTextures(filterNum, fTexture, 0);
        for (int i = 0; i < filterNum; i++) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fTexture[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height,
                    0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        }

    }

    //取消绑定Texture
    private void unBindFrame() {
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }


    private void deleteFrameBuffer() {
        GLES30.glDeleteRenderbuffers(1, mFrameBufferTextures, 0);
        GLES30.glDeleteFramebuffers(1, mFrameBuffers, 0);
        GLES30.glDeleteTextures(1, fTexture, 0);
    }

}
