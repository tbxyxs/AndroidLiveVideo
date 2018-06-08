package com.android.tolin.app.live.presenter;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.android.tolin.app.live.filter.AbsFilter;
import com.android.tolin.app.live.camera.Size;
import com.android.tolin.app.live.utils.CameraHelper;
import com.android.tolin.app.live.utils.GLShaderHelper;
import com.android.tolin.app.live.view.LiveGLSurfaceView;
import com.android.tolin.app.live.view.LiveRenderer;
import com.android.tolin.app.live.view.PSurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LivePresenter extends AbsPresenter implements ILive, GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private LiveGLSurfaceView mGLSurfaceView;
    private CameraHelper cameraHelper;
    private LiveRenderer liveRender;
    private int oesTextureID;
    private PSurfaceTexture mSurface;
    private String cameraId = "1";
    private boolean using = false;
    private GLSurfaceView.Renderer callBackRendener;

    public LivePresenter(LiveGLSurfaceView glSurfaceView) {
        this.mGLSurfaceView = glSurfaceView;
        initGL();
    }

    private void initGL() {
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //Log.v("surface1", "onSurfaceCreated");
        oesTextureID = GLShaderHelper.createOESTextureObject();
        mSurface = new PSurfaceTexture(oesTextureID);
        mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //每获取到一帧数据时请求OpenGL ES进行渲染
                mGLSurfaceView.requestRender();
            }
        });
        mSurface.setOnFrameAvailableListener(this);
        liveRender = new LiveRenderer(mGLSurfaceView.getContext(), mSurface, cameraId);
        cameraHelper = new CameraHelper(mGLSurfaceView, cameraId, mSurface);
        Size preSize = cameraHelper.getCamera().getCameraPreviewDataSize();
        liveRender.onSurfaceCreated(gl, config);
        liveRender.setCameraDataSize(preSize.getHeight(), preSize.getWidth());
        if (callBackRendener != null) {
            callBackRendener.onSurfaceCreated(gl, config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Log.v("surface1", "onSurfaceChanged");
        liveRender.setPreviewViewSize(width, height);
        liveRender.onSurfaceChanged(gl, width, height);
        cameraHelper.startPreview();

        if (callBackRendener != null) {
            callBackRendener.onSurfaceChanged(gl, width, height);
        }
        using = false;

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        liveRender.onDrawFrame(gl);
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
        if (null != liveRender) {
//            liveRender.destroy();
            liveRender = null;
        }
    }

    public void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.destroy();
            cameraHelper = null;
        }
        if (null != liveRender) {
//            liveRender.destroy();
            liveRender = null;
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
        liveRender.addFilter(filter);
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
//            liveRender.destroy();
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
