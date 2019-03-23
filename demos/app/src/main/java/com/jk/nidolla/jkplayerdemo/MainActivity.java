package com.jk.nidolla.jkplayerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private IMediaPlayer mPlayer;
    private Button mPlayBtn;
    private long mPosition = -1;
    private String mVideoPath = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private OnStateChangeListener mPlayerListener;

    final static int STATE_IDLE = -1;
    final static int STATE_PREPARING = 0;
    final static int STATE_PREPARED = 1;
    final static int STATE_PLAYING = 2;
    final static int STATE_PAUSED = 3;
    final static int STATE_STOPPED = 4;
    final static int STATE_COMPLETED = 5;
    final static int STATE_ERROR = 6;
    private int mPlayerState = STATE_IDLE;
    private boolean mSurfaceReady = false;


    class OnStateChangeListener implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(IMediaPlayer player, int what, int extra) {
            Log.d(TAG, "OnStateChangeListener, onInfo(), what=" + what + ",extra=" + extra);
            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer player) {
            Log.d(TAG, "OnStateChangeListener, onPrepared()");
            mPlayerState = STATE_PREPARED;
            if (mPlayer != null) {
                mPlayer.start();
                mPlayBtn.setText(getResources().getString(R.string.playing));
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initPlayer();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayBtn.setText(getResources().getString(R.string.play));
            mPlayerState = STATE_PAUSED;
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if (mPlayer != null && mPlayerState == STATE_PAUSED) {
            mPlayer.start();
            mPlayBtn.setText(getResources().getString(R.string.playing));
            mPlayerState = STATE_PLAYING;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    private void bindViews() {
        mSurfaceView = findViewById(R.id.surfaceview);
        createSurface();
        mPlayBtn = (Button) findViewById(R.id.play);
        mPlayBtn.setOnClickListener(this);
    }

    private void initPlayer() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        mPlayer = new IjkMediaPlayer();
        mPlayer.setLooping(true);
        mPlayerListener = new OnStateChangeListener();
        setListener(mPlayer);
        try {
            mPlayer.setDataSource(mVideoPath);
        } catch (IOException e) {
            e.printStackTrace();
            this.finish();
        }
    }

    private void startPlay() {
        if (mPlayer != null && mPlayerState == STATE_IDLE && mSurfaceReady) {
            mPlayer.prepareAsync();
            mPlayerState = STATE_PREPARING;
        }
    }

    private void setListener(IMediaPlayer player) {
        player.setOnPreparedListener(mPlayerListener);
        player.setOnInfoListener(mPlayerListener);
    }
    private void createSurface() {
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceCreated, mPosition=" + mPosition);
                if (mPlayer != null) {
                    mPlayer.seekTo(mPosition);
                    mPlayer.setSurface(surfaceHolder.getSurface());
                    mSurfaceReady = true;
                    startPlay();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceDestroyed");
                mSurfaceReady = false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (null != mPlayer) {
                    Log.d(TAG, "mPlayerState=" + mPlayerState + ", mPlayer.isPlaying()=" + mPlayer.isPlaying());
                    if (mPlayerState == STATE_PAUSED) {
                        mPlayer.start();
                        mPlayBtn.setText(getResources().getString(R.string.playing));
                        mPlayerState = STATE_PLAYING;
                    } else if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                        mPlayBtn.setText(getResources().getString(R.string.play));
                        mPlayerState = STATE_PAUSED;
                    } else {
                        startPlay();
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
