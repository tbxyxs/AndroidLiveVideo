package com.android.tolin.app.live.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.support.annotation.IdRes;
import android.util.Log;


public class GLShaderHelper {
    public static boolean DEBUG = true;
    private static String TAG = GLShaderHelper.class.getSimpleName();

    public static void useTexParameter() {
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
    }

    public static void useTexParameter(int gl_wrap_s, int gl_wrap_t, int gl_min_filter,
                                       int gl_mag_filter) {
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, gl_wrap_s);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, gl_wrap_t);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, gl_min_filter);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, gl_mag_filter);
    }

    public static void genTexturesWithParameter(int size, int[] textures, int start,
                                                int gl_format, int width, int height) {
        GLES30.glGenTextures(size, textures, start);
        for (int i = 0; i < size; i++) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, gl_format, width, height,
                    0, gl_format, GLES30.GL_UNSIGNED_BYTE, null);
            useTexParameter();
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    public static void bindFrameTexture(int frameBufferId, int textureId) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, textureId, 0);
    }

    public static void unBindFrameBuffer() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    /**
     * 创建一个适用于bitmap的纹理
     *
     * @param context
     * @param resId   图像资源id
     */
    public static int createTextureForBitmap(Context context, @IdRes int resId) {
        int[] textureId = new int[1];
        GLES30.glGenTextures(1, textureId, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null) {
            GLES30.glDeleteTextures(1, textureId, 0);
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);//0：解除纹理的绑定
        return textureId[0];
    }

    /**
     * 创建一个OES类型的材质id
     *
     * @return
     */
    public static int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES30.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }

    /**
     * 创建一个纹理id.
     *
     * @return
     */
    public static int createTextureId() {
        int[] texture = new int[1];
        GLES30.glGenTextures(1, texture, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        if (texture[0] == 0) {
            throw new RuntimeException("create textureid error!");
        }
        return texture[0];
    }

    public static boolean validateProgram(int programId) {
        GLES30.glValidateProgram(programId);
        final int[] validateStatus = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_VALIDATE_STATUS, validateStatus, 0);
        glError(1, "link program error:" + GLES30.glGetProgramInfoLog(programId));
        return validateStatus[0] != 0;
    }

    /**
     * 创建program程序，并将顶点、片断着色器链接到program中。
     *
     * @param vertexId   顶点着色器。
     * @param fragmentId 片断着色器。
     * @return 0：失败
     */
    public static int createProgram(int vertexId, int fragmentId) {
        int programId = GLES30.glCreateProgram();
        if (programId == 0
                || vertexId == 0
                || fragmentId == 0) {
            throw new RuntimeException("create program error!");
        }
        linkShaderToProgram(programId, vertexId, fragmentId);
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES30.GL_TRUE) {
            glError(1, "link program error:" + GLES30.glGetProgramInfoLog(programId));
            GLES30.glDeleteProgram(programId);
            programId = 0;
        }
        return programId;
    }

    /**
     * 链接顶点着色器与片段着色器到program程序中。
     *
     * @param vertexId   顶点shader id
     * @param fragmentId 片段shader id.
     * @return
     */
    private static void linkShaderToProgram(int programId, int vertexId, int fragmentId) {
        GLES30.glAttachShader(programId, vertexId);
        GLES30.glAttachShader(programId, fragmentId);
        GLES30.glLinkProgram(programId);
    }

    /**
     * 创建一个shader对象。
     *
     * @param shaderType
     * @return 0：创建失败；
     */
    public static int createShader(int shaderType, String shaderSource) {
        int shaderId = GLES30.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("create shader <shaderType=" + shaderType + "> error!");
        }
        bindShaderSource(shaderId, shaderSource);
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {//判断着色器是否与新的shaderSource编译成功。0：失败
            glError(1, "Compile shader error:" + shaderType);
            glError(1, "GLES30 Error:" + GLES30.glGetShaderInfoLog(shaderId)); //获取着色器对应的日志信息。
            GLES30.glDeleteShader(shaderId);
            shaderId = 0;
        }
        return shaderId;
    }

    /**
     * 绑定shader对应的.glsl源码
     *
     * @param shaderId
     * @param shaderSource glsl源码
     */
    private static void bindShaderSource(int shaderId, String shaderSource) {
        GLES30.glShaderSource(shaderId, shaderSource);//绑定shader 源码。
        GLES30.glCompileShader(shaderId); //编译shader
    }

    public static void glError(int code, Object index) {
        if (DEBUG && code != 0) {
            Log.e(TAG, "glError:" + code + "---" + index);
        }
    }

}
