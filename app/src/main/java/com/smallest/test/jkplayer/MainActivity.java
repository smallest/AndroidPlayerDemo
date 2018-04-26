package com.smallest.test.jkplayer;

import android.os.Build;
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
