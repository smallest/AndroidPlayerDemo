package com.smallest.test.jkplayer.player;

import android.view.Surface;

public interface IMediaPlayer {
    interface OnStateChangeListener {
        void onPrepared();
    }
    void setMedia(IMedia media);
    void setSurface(Surface surface);
    void setLooping(boolean looping);
    void setSpeed(float speed);
    void setScreenOnWhilePlaying(boolean screenOn);
    void setAudioStreamType(int streamtype);
    void setVolume(float leftVolume, float rightVolume);
    void prepareAsync();
    void start();
    void pause();
    void stop();
    void release();
    void seekTo(int position);
    boolean isPlaying();
    boolean isPaused();
    int getCurrentPosition();
    int getDuration();
    int getVideoWidth();
    int getVideoHeight();

}
