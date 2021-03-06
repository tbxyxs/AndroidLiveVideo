package com.android.tolin.app.live.filter;

import android.content.res.Resources;
import android.opengl.GLES30;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description:
 */
public class GroupFilter extends AbsFilter{

    private Queue<AbsFilter> mFilterQueue;
    private List<AbsFilter> mFilters;
    private int width=0, height=0;
    private int size=0;

    public GroupFilter(Resources res) {
        super(res);
        mFilters=new ArrayList<>();
        mFilterQueue=new ConcurrentLinkedQueue<>();
    }

    @Override
    protected void initBuffer() {

    }

    public void addFilter(final AbsFilter filter){
        mFilterQueue.add(filter);
    }

    public boolean removeFilter(AbsFilter filter){
        boolean b=mFilters.remove(filter);
        if(b){
            size--;
        }
        return b;
    }

    public AbsFilter removeFilter(int index){
        AbsFilter f=mFilters.remove(index);
        if(f!=null){
            size--;
        }
        return f;
    }

    public void clearAll(){
        mFilterQueue.clear();
        mFilters.clear();
        size=0;
    }

    /**
     * 双Texture,一个输入一个输出,循环往复
     */
    public void draw(){
        updateFilter();
        textureIndex=0;
        GLES30.glViewport(0,0,width,height);

        for (AbsFilter filter:mFilters){
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fFrame[0]);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, fTexture[textureIndex%2], 0);
            GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT,
                GLES30.GL_RENDERBUFFER, fRender[0]);
            if(textureIndex==0){
                filter.setTextureId(getTextureId());
            }else{
                filter.setTextureId(fTexture[(textureIndex-1)%2]);
            }
            filter.draw();
            unBindFrame();
            textureIndex++;
        }
    }

    private void updateFilter(){
        AbsFilter f;
        while ((f=mFilterQueue.poll())!=null){
            f.create();
            f.setSize(width,height);
            mFilters.add(f);
            size++;
        }
    }

    @Override
    public int getOutputTexture(){
        return size==0?getTextureId():fTexture[(textureIndex-1)%2];
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.width=width;
        this.height=height;
        updateFilter();
        createFrameBuffer();
    }

    //创建离屏buffer
    private int fTextureSize = 2;
    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[fTextureSize];
    private int textureIndex=0;

    //创建FrameBuffer
    private boolean createFrameBuffer() {
        GLES30.glGenFramebuffers(1, fFrame, 0);
        GLES30.glGenRenderbuffers(1, fRender, 0);

        genTextures();
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fFrame[0]);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, fRender[0]);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, width,
            height);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D, fTexture[0], 0);
        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT,
            GLES30.GL_RENDERBUFFER, fRender[0]);
//        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
//        if(status==GLES30.GL_FRAMEBUFFER_COMPLETE){
//            return true;
//        }
        unBindFrame();
        return false;
    }

    //生成Textures
    private void genTextures() {
        GLES30.glGenTextures(fTextureSize, fTexture, 0);
        for (int i = 0; i < fTextureSize; i++) {
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
        GLES30.glDeleteRenderbuffers(1, fRender, 0);
        GLES30.glDeleteFramebuffers(1, fFrame, 0);
        GLES30.glDeleteTextures(1, fTexture, 0);
    }

}
