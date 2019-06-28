package com.chipsee.wrfiledemo;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Chipsee
 * on 2019/06/28
 */
public class FileUtils {
    private static final String TAG = "WRFileDemo";

    public static String readRawFile(Context context,int Rid) {
        StringBuilder stringBuilder = new StringBuilder();
        char [] buf = new char[64];
        int count=0;
        try {
            InputStream in = context.getResources().openRawResource(Rid);
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            while ((count = reader.read(buf)) != -1) {
                stringBuilder.append(buf,0,count);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return stringBuilder.toString();
    }

    public static String readAssetsFile(Context context,String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        char [] buf = new char[64];
        int count=0;
        try {
            InputStream in = context.getAssets().open(filePath);
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            while ((count = reader.read(buf)) != -1) {
                stringBuilder.append(buf,0,count);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return stringBuilder.toString();
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        char [] buf = new char[64];
        int count=0;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fileInputStream, "UTF-8");
            while ((count = reader.read(buf)) != -1) {
                stringBuilder.append(buf,0,count);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return stringBuilder.toString();
    }

    public static void writeToFile(String filePath,String content){
        File file = getFile(filePath);
        try {
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static File getFile(String filePath) {
        File dir = new File(filePath);
        if (!dir.getParentFile().exists()) {
            dir.getParentFile().mkdirs();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                boolean flag = file.createNewFile();
                if (!flag) {
                    Log.e(TAG, "createNewFile Fail");
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return file;
    }
}
