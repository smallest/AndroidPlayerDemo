package com.smallest.test.jkplayer.codec;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.smallest.test.jkplayer.R;
import com.smallest.test.jkplayer.utils.FileUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


public class MediaCodecTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MediaCodecTestActivity";
    private boolean mHasWriteStoragePermission = false;
    private Button mBtn;
    private String mAACPath = FileUtils.getBaseFolder(MediaCodecTestActivity.this) + "demo1.aac";
    private String mPCMPath = FileUtils.getBaseFolder(MediaCodecTestActivity.this) + "demo1.pcm";
    private boolean mAACToPCMRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediacodectest);
        mBtn = (Button)findViewById(R.id.audio_change);
        mBtn.setOnClickListener(this);
        requestPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_change:
                if (!mHasWriteStoragePermission) {
                    requestPermission();
                }
                if (false == mAACToPCMRunning) {
                    mAACToPCMRunning = true;
                    AACToPCM aacToPCM = new AACToPCM();
                    int ret = aacToPCM.decodeAACToPCM(mAACPath, mPCMPath);
                    Log.d(TAG, "decodeAACToPCM finished, ret=" + ret);
                } else {
                    Log.d(TAG, "task AACToPCM is running");
                }
                break;
            default:
                break;
        }
    }

    private void requestPermission(){
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            mHasWriteStoragePermission = true;
                            Log.d(TAG, "testRxPermission CallBack onPermissionsGranted() : " + permission.name +
                                    " request granted , to do something...");

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            mHasWriteStoragePermission = false;
                            Log.d(TAG, "testRxPermission CallBack onPermissionsDenied() : " + permission.name + "request denied");
                            Toast.makeText(MediaCodecTestActivity.this, "拒绝权限，等待下次询问哦", Toast.LENGTH_SHORT).show();
                        } else {
                            mHasWriteStoragePermission = false;
                            Log.d(TAG, "testRxPermission CallBack onPermissionsDenied() : this " + permission.name + " is denied " +
                                    "and never ask again");
                            Toast.makeText(MediaCodecTestActivity.this, "拒绝权限，不再弹出询问框，请前往APP应用设置中打开此权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}