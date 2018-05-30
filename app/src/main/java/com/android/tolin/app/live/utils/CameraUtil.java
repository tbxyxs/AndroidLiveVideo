package com.android.tolin.app.live.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;

import com.android.tolin.app.live.camera.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtil {

    /**
     * 检查设备是否有摄像头。
     *
     * @param context
     * @return true：有
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * 获取相机与指定宽高相对应的最佳预览分辨率（最接近的分辨率，并不按宽高比率进行比较）
     *
     * @param sizes  相机支持的宽高
     * @param width  指定宽
     * @param height 指定高
     * @return
     */
    public static Size chooseClosePreviewSize(Context context, List<Size> sizes, int width, int height) {
        List<Size> collectorSizes = new ArrayList<>();
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        int reqTmpWidth;
        int reqTmpHeight;
//        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (ori == mConfiguration.ORIENTATION_PORTRAIT) {  //竖屏
            reqTmpWidth = height;
            reqTmpHeight = width;
        } else {
            reqTmpWidth = width;
            reqTmpHeight = height;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Size size : sizes) {
            if ((size.getWidth() == reqTmpWidth) && (size.getHeight() == reqTmpHeight)) {
                return size;
            }
        }
        for (Size option : sizes) {
            if (reqTmpWidth < reqTmpHeight) {
                if (option.getWidth() > reqTmpWidth && option.getHeight() > reqTmpHeight) {
                    collectorSizes.add(option);
                }
            } else {
                if (option.getHeight() > reqTmpWidth && option.getWidth() > reqTmpHeight) {
                    collectorSizes.add(option);
                }
            }
        }
//        for (Size option : sizes) {
//            if (width > height) {
//                if (option.getWidth() > width && option.getHeight() > height) {
//                    collectorSizes.add(option);
//                }
//            } else {
//                if (option.getHeight() > width && option.getWidth() > height) {
//                    collectorSizes.add(option);
//                }
//            }
//        }
        if (collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new Comparator<Size>() {
                @Override
                public int compare(Size s1, Size s2) {
                    return Long.signum(s1.getWidth() * s1.getHeight() - s2.getWidth() * s2.getHeight());
                }
            });
        }
        return sizes.get(0);
    }

    /**
     * 通过对比得到与宽高比最接近的尺寸（如果有相同尺寸，优先选择,但它可能存在会找到一个比率相同，但分辨率很低的尺寸）
     *
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   camera支持的的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Size chooseRatioPreviewSize(Context context, int surfaceWidth, int surfaceHeight, List<Size> preSizeList) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (ori == mConfiguration.ORIENTATION_PORTRAIT) {  //竖屏
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Size size : preSizeList) {
            if ((size.getWidth() == reqTmpWidth) && (size.getHeight() == reqTmpHeight)) {
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Size retSize = null;
        for (Size size : preSizeList) {
            curRatio = ((float) size.getWidth()) / size.getHeight();
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

    public static List<Size> convertSize(android.util.Size[] oldSizes) {
        if (oldSizes.length <= 0) return null;
        List<Size> sizes = new ArrayList<>();
        for (android.util.Size size : oldSizes) {
            Size temp = new Size(size);
            sizes.add(temp);
        }
        return sizes;
    }

    public static List<Size> convertSize(Camera.Size[] oldSizes) {
        if (oldSizes.length <= 0) return null;
        List<Size> sizes = new ArrayList<>();
        for (Camera.Size size : oldSizes) {
            Size temp = new Size(size);
            sizes.add(temp);
        }
        return sizes;
    }
}
