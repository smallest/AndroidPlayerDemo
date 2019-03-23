package com.smallest.test.jkplayer.player;

import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.smallest.test.jkplayer.R;

public class JKPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = JKPlayerActivity.class.getSimpleName();
    private final static int MSG_PROGRESS = 10001;
    private final static long DELAY_GET_PROGRESS = 1000;
    private SurfaceView mSurfaceView;
    private IMediaPlayer mPlayer;
    private IMedia mMedia;
    private Button mPlayBtn;
    private int mPosition = -1;
    private Handler mHandler;

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS:
                    if (null != mPlayer){
                        Log.d(TAG, "current position:" + mPlayer.getCurrentPosition());
                        Log.d(TAG, "duration:" + mPlayer.getDuration());
                        Log.d(TAG, "width:" + mPlayer.getVideoWidth() + ",height=" + mPlayer.getVideoHeight());
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_GET_PROGRESS);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jkplayer);
        testMediaCodecCapability();
        bindViews();
        initPlayer();
        mHandler = new MyHandler(getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mHandler.removeMessages(MSG_PROGRESS);
            mPlayBtn.setText(getResources().getString(R.string.play));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null && mPlayer.isPaused()) {
            mPlayer.start();
            mHandler.sendEmptyMessage(MSG_PROGRESS);
            mPlayBtn.setText(getResources().getString(R.string.playing));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    private void bindViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        createSurface();
        setSpeedOptions();
        mPlayBtn = (Button) findViewById(R.id.play);
        mPlayBtn.setOnClickListener(this);
    }

    private void initPlayer() {
        String uri = "android.resource://" + getPackageName() + "/raw/jack" ;
        Log.d(TAG, "uri=" + uri);

        mPlayer = new JKPlayer(getApplicationContext());
        mPlayer.setLooping(true);
        mPlayer.setScreenOnWhilePlaying(true);
        mMedia = new Media(uri);
        mPlayer.setMedia(mMedia);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setVolume(0.3f, 0.8f);
    }

    private void createSurface() {
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceCreated, mPosition=" + mPosition);
                if (mPlayer != null) {
                    mPlayer.seekTo(mPosition);
                    mPlayer.setSurface(surfaceHolder.getSurface());
                    mPlayer.prepareAsync();
                    mPlayer.start();
                    mHandler.sendEmptyMessage(MSG_PROGRESS);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceDestroyed");
                if(mPlayer != null){
                    mPosition = mPlayer.getCurrentPosition();
                    mPlayer.stop();
                }
            }
        });
    }

    // speed values displayed in the spinner
    private String[] getSpeedStrings() {
        return new String[]{"0", "0.2", "0.4", "0.6", "0.8", "1.0", "1.2", "1.4", "1.6", "1.8", "2.0"};
    }

    private void setSpeedOptions() {
        final Spinner speedOptions = (Spinner)findViewById(R.id.speedOptions);
        String[] speeds = getSpeedStrings();

        ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, speeds);
        speedOptions.setAdapter(arrayAdapter);

        // change player playback speed if a speed is selected
        speedOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != mPlayer) {
                    float selectedSpeed = Float.parseFloat(
                            speedOptions.getItemAtPosition(i).toString());
                    mPlayer.setSpeed(selectedSpeed);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (null != mPlayer) {
                    Log.d(TAG, "mPlayer.isPaused()=" + mPlayer.isPaused() + ", mPlayer.isPlaying()=" + mPlayer.isPlaying());
                    if (mPlayer.isPaused()) {
                        mPlayer.start();
                        mHandler.sendEmptyMessage(MSG_PROGRESS);
                        mPlayBtn.setText(getResources().getString(R.string.playing));
                    } else if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                        mHandler.removeMessages(MSG_PROGRESS);
                        mPlayBtn.setText(getResources().getString(R.string.play));
                    } else {
                        mPlayer.prepareAsync();
                        mPlayer.start();
                        mHandler.sendEmptyMessage(MSG_PROGRESS);
                        mPlayBtn.setText(getResources().getString(R.string.playing));
                    }
                }
                break;
            default:
                break;
        }
    }
    void testMediaCodecCapability() {
        int n = MediaCodecList.getCodecCount();
        for (int i = 0; i < n; ++i) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            String[] supportedTypes = info.getSupportedTypes();
            for (int j = 0; j < supportedTypes.length; ++j) {
                Log.d(TAG, "codec info:" + info.getName() + ", supportedTypes:" + supportedTypes[j]);
            }
        }
    }
}
