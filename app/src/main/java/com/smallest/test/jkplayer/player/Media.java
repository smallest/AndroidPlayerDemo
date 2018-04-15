package com.smallest.test.jkplayer.player;

public class Media implements IMedia{
    private String mUri;

    public Media(String uri) {
        mUri = uri;
    }

    @Override
    public String getUri() {
        return mUri;
    }

    @Override
    public String toString() {
        return "@Media(uri=" + mUri + ")";
    }
}
