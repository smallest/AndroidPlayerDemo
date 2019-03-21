package com.smallest.test.jkplayer.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

public class JKPlayer implements IMediaPlayer,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnVideoSizeChangedListener {
    private String TAG = JKPlayer.class.getSimpleName();
    private Context mContext;
    private IMedia mMedia;
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private boolean mToggleStart;
    private boolean mIsPaused = false;
    private boolean mLooping = false;
    private float mSpeed = 1;

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
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
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
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "duration=" + mMediaPlayer.getDuration());
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
    public void setSpeed(float speed) {
        Log.d(TAG, "setSpeed(), speed=" + speed);
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
//                mMediaPlayer.pause();
            }
            mSpeed = speed;
        } else {
            Log.w(TAG, "setSpeed not supported!");
        }
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        if (mCurrentState != STATE_ERROR) {
            mMediaPlayer.setAudioStreamType(streamtype);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mCurrentState != STATE_ERROR) {
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
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
    public int getDuration() {
        if (mCurrentState != STATE_ERROR) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getVideoWidth() {
        if (mCurrentState != STATE_ERROR) {
            return mMediaPlayer.getVideoWidth();
        }
        return -1;
    }

    @Override
    public int getVideoHeight() {
        if (mCurrentState != STATE_ERROR) {
            return mMediaPlayer.getVideoHeight();
        }
        return -1;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mCurrentState = STATE_ERROR;
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete(), currentPosition=" + mp.getCurrentPosition());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        Log.d(TAG, "onBufferingUpdate(), percent=" + percent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onCompletion()");
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        Log.d(TAG, "onInfo(), what=" + what + ", extra=" + extra);
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged(), width=" + width + ", height=" + height);
    }
}
