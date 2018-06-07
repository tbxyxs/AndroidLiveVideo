package com.android.tolin.app.live.presenter;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.android.tolin.app.live.camera.Size;
import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.utils.CameraHelper;
import com.android.tolin.app.live.utils.Gl2Utils;
import com.android.tolin.app.live.view.CameraGLSurfaceView;
import com.android.tolin.app.live.view.CameraRenderer;
import com.android.tolin.app.live.view.PSurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraPresenter extends AbsPresenter implements ILive, GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private CameraGLSurfaceView mGLSurfaceView;
    private CameraHelper cameraHelper;
    private CameraRenderer cameraRender;
    private int mTextureID;
    private PSurfaceTexture mSurface;
    private String cameraId = "1";
    private boolean using = false;
    private GLSurfaceView.Renderer callBackRendener;

    public CameraPresenter(CameraGLSurfaceView glSurfaceView) {
        this.mGLSurfaceView = glSurfaceView;
        initGL();

    }

    private void initGL() {
        boolean isHard = mGLSurfaceView.isHardwareAccelerated();
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //Log.v("surface1", "onSurfaceCreated");
        mTextureID = Gl2Utils.createTextureID();
        mSurface = new PSurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        cameraRender = new CameraRenderer(mGLSurfaceView.getContext(), mSurface, cameraId);
        cameraHelper = new CameraHelper(mGLSurfaceView, cameraId, mSurface);
        Size preSize = cameraHelper.getCamera().getCameraPreviewDataSize();
        cameraRender.onSurfaceCreated(gl, config);
        cameraRender.setCameraDataSize(preSize.getHeight(), preSize.getWidth());
        if (callBackRendener != null) {
            callBackRendener.onSurfaceCreated(gl, config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Log.v("surface1", "onSurfaceChanged");
        cameraRender.setPreviewViewSize(width, height);
        cameraRender.onSurfaceChanged(gl, width, height);
        cameraHelper.startPreview();

        if (callBackRendener != null) {
            callBackRendener.onSurfaceChanged(gl, width, height);
        }
        using = false;

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        cameraRender.onDrawFrame(gl);
        if (callBackRendener != null) {
            callBackRendener.onDrawFrame(gl);
        }
    }

    /**
     * @return true：使用中
     */
    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public void onPause() {
        if (cameraHelper != null) {
            cameraHelper.destroy();
            cameraHelper = null;
        }
        if (null != cameraRender) {
            cameraRender.destroy();
            cameraRender = null;
        }
    }

    public void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.destroy();
            cameraHelper = null;
        }
        if (null != cameraRender) {
            cameraRender.destroy();
            cameraRender = null;
        }
        mSurface = null;
        mGLSurfaceView = null;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraId() {
        cameraId = cameraHelper.getCameraId();
        return cameraId;
    }


    @Override
    public void takePic() {

    }

    @Override
    public void recVideo() {

    }

    @Override
    public void addFilter(AbsFilter filter) {
        cameraRender.addFilter(filter);
    }

    @Override
    public void switchCamera() {
        if (cameraHelper.getCameraCount() > 1) {
            if ("0".equals(cameraHelper.getCameraId())) {
                setCameraId("1");
            } else {
                setCameraId("0");
            }
            using = true;
            cameraHelper.destroy();
            cameraRender.destroy();
            mGLSurfaceView.onPause();
            mGLSurfaceView.onResume();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mGLSurfaceView != null) {
            mGLSurfaceView.requestRender();
        }
    }

    /**
     * 设置rendener回调周期
     *
     * @param rendener
     */
    public void setCallBackRendener(GLSurfaceView.Renderer rendener) {
        this.callBackRendener = rendener;
    }
}
