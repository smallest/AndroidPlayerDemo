package com.smallest.test.jkplayer.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

public class JKPlayer implements IMediaPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private String TAG = JKPlayer.class.getSimpleName();
    private Context mContext;
    private IMedia mMedia;
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private boolean mToggleStart;
    private boolean mIsPaused = false;
    private boolean mLooping = false;

    private int STATE_IDLE = -1;
    private int STATE_INITIALIZED = 0;
    private int STATE_PREPARING = 1;
    private int STATE_PREPARED = 2;
    private int STATE_STARTED = 3;
    private int STATE_STOPPED = 4;
    private int STATE_ERROR = 5;
    private int mCurrentState = STATE_IDLE;

    private int mSeekPosition = -1;

    public JKPlayer(Context context) {
        Log.d(TAG, "JKPlayer()");
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }
    @Override
    public void prepareAsync() {
        Log.d(TAG, "prepareAsync(), mCurrentState=" + mCurrentState);
        if (null == mSurface || mMedia == null || mMedia.getUri().isEmpty()) {
            Log.d(TAG, "mSurface=" + mSurface + ", mMedia=" + mMedia);
            return;
        }
        if (mCurrentState == STATE_IDLE) {
            try {
                mMediaPlayer.setDataSource(mContext, Uri.parse(mMedia.getUri()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCurrentState = STATE_INITIALIZED;
        }
        if (mCurrentState == STATE_INITIALIZED || mCurrentState == STATE_STOPPED) {
            mMediaPlayer.setLooping(mLooping);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
        }
    }

    @Override
    public void setSurface(Surface surface) {
        Log.d(TAG, "setSurface()");
        mSurface = surface;
        prepareAsync();
    }

    @Override
    public void setMedia(IMedia media) {
        Log.d(TAG, "setMedia()");
        mMedia = media;
    }

    @Override
    public void start() {
        Log.d(TAG, "start(), mCurrentState=" + mCurrentState);
        mToggleStart = true;
        startPlay();
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause(), mCurrentState=" + mCurrentState);
        if (mCurrentState == STATE_STARTED) {
            mCurrentState = STATE_PREPARED;
            mMediaPlayer.pause();
            mIsPaused = true;
        }
    }

    private void startPlay() {
        Log.d(TAG, "startPlay(), mToggleStart=" + mToggleStart + ",mCurrentState=" + mCurrentState + ",mSeekPosition=" + mSeekPosition);
        if (mToggleStart && mCurrentState == STATE_PREPARED) {
            mMediaPlayer.start();
            if (mSeekPosition >= 0) {
                Log.d(TAG, "mMediaPlayer.seekTo(), mSeekPosition=" + mSeekPosition);
                mMediaPlayer.seekTo(mSeekPosition);
                mSeekPosition = -1;
            }
            mCurrentState = STATE_STARTED;
            mIsPaused = false;
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop()");
        if (mCurrentState != STATE_IDLE && mCurrentState != STATE_INITIALIZED && mCurrentState != STATE_ERROR) {
            Log.d(TAG, "call mediaPlayer stop()");
            mMediaPlayer.stop();
            Log.d(TAG, "call mediaPlayer stop() end");
            mCurrentState =  STATE_STOPPED;
        }
    }

    @Override
    public void release() {
        Log.d(TAG, "release()");
        mMediaPlayer.release();
        mSeekPosition = -1;
        mCurrentState = STATE_IDLE;
    }

    @Override
    public void seekTo(int position) {
        Log.d(TAG, "seekTo(), position=" + position);
        if (position < 0) {
            return;
        }
        if (mCurrentState != STATE_IDLE && mCurrentState != STATE_INITIALIZED &&
                mCurrentState != STATE_STOPPED  && mCurrentState != STATE_ERROR) {
            mMediaPlayer.seekTo(position);
        } else {
            mSeekPosition = position;
        }
    }

    @Override
    public void setLooping(boolean looping) {
        Log.d(TAG, "setLooping(), looping=" + looping);
        mLooping = looping;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared(), mToggleStart=" + mToggleStart);
        mCurrentState = STATE_PREPARED;
        startPlay();
    }

    @Override
    public boolean isPlaying() {
        if (mCurrentState != STATE_ERROR) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
    @Override
    public boolean isPaused() {
        return mIsPaused;
    }

    @Override
    public int getCurrentPosition() {
        if (mCurrentState != STATE_ERROR) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mCurrentState = STATE_ERROR;
        return false;
    }
}