package com.smallest.test.jkplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.smallest.test.jkplayer.player.IMedia;
import com.smallest.test.jkplayer.player.IMediaPlayer;
import com.smallest.test.jkplayer.player.JKPlayer;
import com.smallest.test.jkplayer.player.Media;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = MainActivity.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private IMediaPlayer mPlayer;
    private IMedia mMedia;
    private Button mPlayBtn;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayBtn.setText(getResources().getString(R.string.play));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null && mPlayer.isPaused()) {
            mPlayer.start();
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
        mSurfaceView = findViewById(R.id.surfaceview);
        createSurface();
        mPlayBtn = findViewById(R.id.play);
        mPlayBtn.setOnClickListener(this);
    }

    private void initPlayer() {
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.roger480;
        Log.d(TAG, "uri=" + uri);

        mPlayer = new JKPlayer(getApplicationContext());
        mPlayer.setLooping(true);
        mMedia = new Media(uri);
        mPlayer.setMedia(mMedia);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (null != mPlayer) {
                    Log.d(TAG, "mPlayer.isPaused()=" + mPlayer.isPaused() + ", mPlayer.isPlaying()=" + mPlayer.isPlaying());
                    if (mPlayer.isPaused()) {
                        mPlayer.start();
                        mPlayBtn.setText(getResources().getString(R.string.playing));
                    } else if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                        mPlayBtn.setText(getResources().getString(R.string.play));
                    } else {
                        mPlayer.prepareAsync();
                        mPlayer.start();
                        mPlayBtn.setText(getResources().getString(R.string.playing));
                    }
                }
                break;
            default:
                break;
        }
    }

}
