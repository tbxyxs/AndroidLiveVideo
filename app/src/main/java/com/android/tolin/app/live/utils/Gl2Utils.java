/*
 *
 * FastDrawerHelper.java
 *
 * Created by Wuwang on 2016/11/17
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.android.tolin.app.live.utils;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Description:
 */
public class Gl2Utils {

    public static final String TAG = "GLUtils";
    public static boolean DEBUG = true;

    public static final int TYPE_FITXY = 0;
    public static final int TYPE_CENTERCROP = 1;
    public static final int TYPE_CENTERINSIDE = 2;
    public static final int TYPE_FITSTART = 3;
    public static final int TYPE_FITEND = 4;

    private Gl2Utils() {

    }


    /**
     * 创建一个外部纹理,用于接收预览数据
     *
     * @return
     */
    public static int createTextureID() {
        int[] texture = new int[1];
        GLES30.glGenTextures(1, texture, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public static void getShowMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int
            viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            } else {
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static void getMatrix(float[] matrix, int type, int imgWidth, int imgHeight, int viewWidth,
                                 int viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (type == TYPE_FITXY) {
                Matrix.orthoM(projection, 0, -1, 1, -1, 1, 1, 3);
                Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
                Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
            }
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            if (sWhImg > sWhView) {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 1, 1 - 2 * sWhImg / sWhView, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, -1, 1, -1, 2 * sWhImg / sWhView - 1, 1, 3);
                        break;
                }
            } else {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 2 * sWhView / sWhImg - 1, -1, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, 1 - 2 * sWhView / sWhImg, 1, -1, 1, 1, 3);
                        break;
                }
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static void getCenterInsideMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int
            viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            } else {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static float[] rotate(float[] m, float angle) {
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return m;
    }

    public static float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    public static float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }

    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = mRes.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }

    public static int createGlProgramByRes(Resources res, String vert, String frag) {
        return createGlProgram(uRes(res, vert), uRes(res, frag));
    }

    //创建GL程序
    public static int createGlProgram(String vertexSource, String fragmentSource) {
        int vertex = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES30.glCreateProgram();
        if (program != 0) {
            GLES30.glAttachShader(program, vertex);
            GLES30.glAttachShader(program, fragment);
            GLES30.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES30.GL_TRUE) {
                glError(1, "Could not link program:" + GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * @param shaderType 着色器类型
     * @param source 着色器glsl脚本
     * @return
     */
    //加载shader
    public static int loadShader(int shaderType, String source) {
        int shader = GLES30.glCreateShader(shaderType);
        if (0 != shader) {
            GLES30.glShaderSource(shader, source);
            GLES30.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                glError(1, "Could not compile shader:" + shaderType);
                glError(1, "GLES30 Error:" + GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static void glError(int code, Object index) {
        if (DEBUG && code != 0) {
            Log.e(TAG, "glError:" + code + "---" + index);
        }
    }

    /**
     * @param angle
     * @param mVerticesData       材质贴图数据参数
     * @param p_width//glviewport 设置的宽高
     * @param p_height
     * @param map_width//贴图宽高
     * @param map_height
     * @return
     */
    public static FloatBuffer setAngle(float angle, float[] mVerticesData, float p_width, float p_height, float map_width, float map_height) {
        float input_center_x = p_width / 2;
        float input_center_y = p_height / 2;//移动位置，中心点再正中间</span>
        float dst_x = (input_center_x - p_width / 2);
        float dst_y = (p_height / 2 - input_center_y);
        float x1 = 0, y1 = 0;
        float x2 = 0, y2 = 0;
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float cos_angle = (float) Math.cos((double) angle);
        float sin_angle = (float) Math.sin((double) angle);
        x1 = ((-map_width / 2) * cos_angle) + ((-map_height / 2) * sin_angle);
        y1 = ((-map_height / 2) * cos_angle) - (-(map_width / 2) * sin_angle);
        x2 = ((map_width / 2) * cos_angle) + ((-map_height / 2) * sin_angle);
        y2 = ((-map_height / 2) * cos_angle) - ((map_width / 2) * sin_angle);
        x3 = ((-map_width / 2) * cos_angle) + ((map_height / 2) * sin_angle);
        y3 = ((map_height / 2) * cos_angle) - ((-map_width / 2) * sin_angle);
        x4 = ((map_width / 2) * cos_angle) + ((map_height / 2) * sin_angle);
        y4 = ((map_height / 2) * cos_angle) - ((map_width / 2) * sin_angle);
        mVerticesData[0] = ((dst_x + x1) / (p_width / 2));
        mVerticesData[1] = ((dst_y + y1) / (p_height / 2));
        mVerticesData[3] = ((dst_x + x2) / (p_width / 2));
        mVerticesData[4] = ((dst_y + y2) / (p_height / 2));

        mVerticesData[6] = ((dst_x + x3) / (p_width / 2));
        mVerticesData[7] = ((dst_y + y3) / (p_height / 2));
        mVerticesData[9] = ((dst_x + x4) / (p_width / 2));
        mVerticesData[10] = ((dst_y + y4) / (p_height / 2));
        FloatBuffer mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mVertices.put(mVerticesData).position(0);
        return mVertices;
    }
//
//    public void roate(){
//        Matrix.rotateM();
//        Matrix.setRotateM();
//        android.graphics.Matrix.
//    }
}
