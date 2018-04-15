package com.smallest.test.jkplayer.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionManager {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE"};
    private static PermissionManager sManager;
    private Context mContext;

    public static PermissionManager getInstance(Context context) {
        if (null == sManager) {
            synchronized (PermissionManager.class) {
                if (null == sManager) {
                    sManager = new PermissionManager(context);
                }
            }
        }
        return sManager;
    }

    private PermissionManager(Context context) {
        mContext = context;
    }

    public boolean checkPermissionToPlay(Activity activity) {
        try {
            //检测是否有读的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有读的权限，去申请读的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }
}
