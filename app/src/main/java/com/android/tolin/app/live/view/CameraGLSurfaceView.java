package com.android.tolin.app.live.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.android.tolin.app.live.camera.Camera1;

public class CameraGLSurfaceView extends GLSurfaceView implements ICameraPre, SurfaceTexture.OnFrameAvailableListener {
    private int mTextureID;
    private SurfaceTexture mSurface;
//    private DirectDrawer mDirectDrawer;
    private Camera1<Camera> camera;
    private Camera1 camera1;

    public CameraGLSurfaceView(Context context) {
        super(context);
        init(context);
    }


    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public SurfaceTexture getmSurface() {
        return mSurface;
    }

    private void init(Context context) {
//         camera1 = new Camera1(this);
//        camera1.open(1);
        // 设置OpenGl ES的版本为3.0
//        setEGLContextClientVersion(2);
//        // 设置与当前GLSurfaceView绑定的Renderer
//        setRenderer(this);
        // 设置渲染的模式
//        setRenderMode(RENDERMODE_WHEN_DIRTY);//被通知的时候才会去渲染视图
//        setRenderMode(RENDERMODE_CONTINUOUSLY);//连续渲染
//        setPreserveEGLContextOnPause(true);
//        mTextureID = GlUtil.createTextureID();
//        mSurface = new SurfaceTexture(mTextureID);
//        mSurface.setOnFrameAvailableListener(this);
//        mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//            @Override
//            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                requestRender();
//            }
//        });

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
//        camera1.preview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //每获取到一帧数据时请求OpenGL ES进行渲染
        requestRender();
    }

//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        mTextureID = GlUtil.createTextureID();
//        mSurface = new android.graphics.SurfaceTexture(mTextureID);
//        mSurface.setOnFrameAvailableListener(this);
//        mDirectDrawer = new DirectDrawer(mTextureID);
//        camera = new Camera1<Camera>();
//        Camera c = camera.open(1);
//        try {
//            c.setPreviewTexture(mSurface);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        gl.glViewport(0, 0, width, height);
//        camera.setPreviewTexture(mSurface);
//        camera.preview();
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        gl.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
//        mSurface.updateTexImage();
//        float[] mtx = new float[16];
//        mSurface.getTransformMatrix(mtx);
//        mDirectDrawer.draw(mtx);
//    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
