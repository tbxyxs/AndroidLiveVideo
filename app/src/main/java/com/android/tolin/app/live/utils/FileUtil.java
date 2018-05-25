package com.android.tolin.app.live.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
    public static String inputStreamToString(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    public static String readAssetsToString(Context context, String assetsFile) {
        InputStream stream = null;
        try {
            stream = context.getApplicationContext().getResources().getAssets().open(assetsFile);
            return inputStreamToString(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("read shader <" + assetsFile + ">error!");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
