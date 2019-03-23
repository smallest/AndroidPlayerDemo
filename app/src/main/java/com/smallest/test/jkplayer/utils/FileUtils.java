package com.smallest.test.jkplayer.utils;

import android.content.Context;
import android.os.Environment;

import com.smallest.test.jkplayer.MyApplication;

import java.io.File;

public class FileUtils {
    private static final String BASE_FOLDER = "smallest";
    public static String getBaseFolder(Context context){
        String baseFolder = Environment.getExternalStorageDirectory() + "/" + BASE_FOLDER + "/";
        File file = new File(baseFolder);
        if (!file.exists()){
            boolean b = file.mkdirs();
            if (!b){
                baseFolder = MyApplication.getContext().getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }
}