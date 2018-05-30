package com.android.tolin.app.live.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import com.android.tolin.app.live.utils.CameraUtil;
import com.android.tolin.app.live.view.AbsGLSurfaceView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 5.0及以上使用的camera为
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2<T extends CameraManager> implements ICamera {
    private final Context context;
    private final AbsGLSurfaceView glSurfaceView;
    private Size mPreviewSize;
    private boolean cameraPermission = false;//true:有权限  false：没有camera相应的权限。
    private SurfaceTexture surfaceTexture;
    private HandlerThread mThreadHandler;
    private Handler mHandler;
    private CameraManager cameraManager;
    private String currCameraId = "0";
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;

    /**
     * 信号量控制器, 防止相机没有关闭时退出本应用(若没有关闭就退出, 会造成其他应用无法调用相机)
     * 当某处获得这个许可时, 其他需要许可才能执行的代码需要等待许可被释放才能获取
     */
    private Semaphore mCameraOpenCloseLock = null;
    private boolean isPreview = false;

    public Camera2(AbsGLSurfaceView glSurfaceView, SurfaceTexture surfaceTexture, String cameraId) {
        this.currCameraId = cameraId;
        this.context = glSurfaceView.getContext().getApplicationContext();
        this.surfaceTexture = surfaceTexture;
        this.glSurfaceView = glSurfaceView;
        initCameraOption();
    }

    private void initCameraOption() {
        //获得CameraManager
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        computerPreviewSize(glSurfaceView);
    }


    //很多过程都变成了异步的了，所以这里需要一个子线程的looper
    private void initLooper() {
        mThreadHandler = new HandlerThread("CameraPreviewThread");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }


    @SuppressLint("MissingPermission")
    private void openCamera(String cameraId) {
        initLooper();
        this.currCameraId = cameraId;
        mCameraOpenCloseLock = new Semaphore(1);
        initCameraOption();
        try {
            // 尝试获得相机开打关闭许可, 等待2500时间仍没有获得则排除异常
            if (!mCameraOpenCloseLock.tryAcquire(3000, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            cameraManager.openCamera(currCameraId, stateCallback, mHandler);
            isPreview = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否有camera权限。
     *
     * @return true：有camera权限。
     */
    public boolean checkCameraPermission() {
        return this.cameraPermission;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview() {
        //打开相机
//        if (!checkCameraPermission()) {
//            return;
//        }
        openCamera(currCameraId);
    }

    @Override
    public void stopPreview() {
        destory();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    @Override
    public void destory() {
        // 获得相机开打关闭许可
        try {
            mCameraOpenCloseLock.acquire();
            // 关闭捕获会话
            if (null != mSession) {
                mSession.close();
                mSession = null;
            }
            // 关闭当前相机
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mCameraOpenCloseLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            mThreadHandler.quitSafely();
            mThreadHandler.join();
            mThreadHandler = null;
            mThreadHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean switchTo(int cameraId) {
        return false;
    }

    @Override
    public void switchCamera() {

    }

    @Override
    public String getCurrCameraId() {
        return currCameraId;
    }

    @Override
    public int cameraCount() {
        try {
            return cameraManager.getCameraIdList().length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isPreview() {
        return isPreview;
    }

    @Override
    public Size getPreviewSize() {
        return computerPreviewSize(glSurfaceView);
    }


    @Override
    public Size computerPreviewSize(AbsGLSurfaceView surfaceView) {
        Size surfaceSize = new Size(glSurfaceView.getWidth(), glSurfaceView.getHeight());
        int preWidth = surfaceSize.width;
        int preHeight = surfaceSize.height;
        Size size = new Size(preWidth, preHeight);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = cameraManager.getCameraCharacteristics(currCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            android.util.Size[] cSizes = map.getOutputSizes(SurfaceTexture.class);
            List<Size> sizes = CameraUtil.convertSize(cSizes);
            mPreviewSize = CameraUtil.getCloselyPreSize(context, preWidth, preHeight, sizes);
            Log.e("Camera2", "OptimalSize width: " + mPreviewSize.getWidth() + " height: " + mPreviewSize.getHeight());
            return new Size(mPreviewSize);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return size;
    }

    private CaptureRequest.Builder mPreviewRequestBuilder;

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

        /**
         * 为相机预览创建新的CameraCaptureSession
         *
         * @param cameraDevice
         */
        private void starPreview(CameraDevice cameraDevice) {
            try {
                surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                Surface surface = new Surface(surfaceTexture);
                //设置了一个具有输出Surface的CaptureRequest.Builder。
                mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface), mSessionStateCallback, mHandler);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            //创建CameraPreviewSession
            starPreview(cameraDevice);
        }


        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    };
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.v("StateCallback", "onConfigured");
            updatePreview(session);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.v("StateCallback", "onConfigureFailed");
            session.close();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void updatePreview(CameraCaptureSession session) {
            mSession = session;
            // 相机已经关闭
            if (null == mCameraDevice) {
                return;
            }
            // 会话准备好后，我们开始显示预览
            try {
                // 自动对焦应
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // 闪光灯
//            setAutoFlash(mPreviewRequestBuilder);
                // 开启相机预览并添加事件
                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                //发送请求
                session.setRepeatingRequest(mPreviewRequest, null, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


}
