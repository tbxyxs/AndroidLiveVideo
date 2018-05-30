package com.android.tolin.app.live.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.android.tolin.app.live.utils.DefaultOption;
import com.android.tolin.app.live.view.AbsGLSurfaceView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 4.4以前的api使用的camera
 */
public class Camera1<T extends Camera> implements ICamera<T> {
    private final static String TAG = Camera1.class.getSimpleName();
    private Camera1Option camera1Option;
    private Camera c = null;
    private SurfaceTexture surfaceTexture;
    private int currCameraId = 0;
    private boolean isPreview = false;


    public Camera1(int cameraId) {
        openCamera(cameraId);
    }

    private Camera openCamera(int cameraId) {
        c = Camera.open(cameraId);
        currCameraId = cameraId;
        camera1Option = new Camera1Option(c);
        return c;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    @Override
    public void startPreview() {
        if (isPreview) {
            return;
        }
        if (c == null) {
            openCamera(Integer.valueOf(getCurrCameraId()));
        }
        if (c != null) {
            try {
                c.setPreviewTexture(surfaceTexture);
                c.startPreview();
                isPreview = true;
            } catch (IOException e) {
                relese();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopPreview() {
        relese();
    }

    @Override
    public void pause() {
        if (null == c) return;
        c.stopPreview();
    }

    @Override
    public void resume() {
        if (null == c) return;
        c.startPreview();
    }

    private void relese() {
        synchronized (Camera1.this) {
            if (c != null) {
                try {
                    c.stopPreview();
                    c.release();
                    c = null;
                    isPreview = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destory() {
        relese();
    }

    @Override
    public boolean switchTo(int cameraId) {
        if (c != null) {
            relese();
        }
        openCamera(cameraId);
        return true;
    }

    @Override
    public void switchCamera() {
        if (cameraCount() > 1) {
            if (currCameraId == 0) {
                switchTo(1);
            } else {
                switchTo(0);
            }
        }
    }

    @Override
    public String getCurrCameraId() {
        return currCameraId + "";
    }

    @Override
    public int cameraCount() {
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        return cameraCount;
    }

    @Override
    public boolean isPreview() {
        return isPreview;
    }


    @Override
    public Size getPreviewDataSize() {
        Point prePoint = camera1Option.getPreSize();
        return new Size(prePoint.x, prePoint.y);
    }

    @Override
    public Size computerPreviewSize(AbsGLSurfaceView surfaceView) {
        return null;
    }


    private static class Camera1Option {
        private final Camera mCamera;
        private final DefaultOption dfOption;
        /**
         * 预览的尺寸
         */
        private Camera.Size preSize;
        /**
         * 实际的尺寸
         */
        private Camera.Size picSize;
        private Point mPreSize;
        private Point mPicSize;

        public Camera1Option(Camera camera) {
            this.mCamera = camera;
            dfOption = new DefaultOption();
            initOption();
        }

        private void initOption() {
            if (mCamera != null) {
                /**选择当前设备允许的预览尺寸*/
                Camera.Parameters param = mCamera.getParameters();
                preSize = getPropPreviewSize(param.getSupportedPreviewSizes(), dfOption.getRate(),
                        dfOption.getPreSize().x);
                picSize = getPropPictureSize(param.getSupportedPictureSizes(), dfOption.getRate(),
                        dfOption.getPreSize().x);
                param.setPictureSize(picSize.width, picSize.height);
                param.setPreviewSize(preSize.width, preSize.height);

                mCamera.setParameters(param);
                Camera.Size pre = param.getPreviewSize();
                Camera.Size pic = param.getPictureSize();
                mPicSize = new Point(pic.height, pic.width);
                mPreSize = new Point(pre.height, pre.width);
            }
        }

        private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
            Collections.sort(list, sizeComparator);

            int i = 0;
            for (Camera.Size s : list) {
                if ((s.height >= minWidth) && equalRate(s, th)) {
                    break;
                }
                i++;
            }
            if (i == list.size()) {
                i = 0;
            }
            return list.get(i);
        }

        private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
            Collections.sort(list, sizeComparator);
            int i = 0;
            for (Camera.Size s : list) {
                if ((s.height >= minWidth) && equalRate(s, th)) {
                    break;
                }
                i++;
            }
            if (i == list.size()) {
                i = 0;
            }
            return list.get(i);
        }

        private static boolean equalRate(Camera.Size s, float rate) {
            float r = (float) (s.width) / (float) (s.height);
            if (Math.abs(r - rate) <= 0.03) {
                return true;
            } else {
                return false;
            }
        }

        private Comparator<Camera.Size> sizeComparator = new Comparator<Camera.Size>() {
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.height == rhs.height) {
                    return 0;
                } else if (lhs.height > rhs.height) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        public Point getPreSize() {
            return mPreSize;
        }

        public Point getPicSize() {
            return mPicSize;
        }
    }
}
