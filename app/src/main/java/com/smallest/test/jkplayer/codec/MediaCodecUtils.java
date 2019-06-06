package com.smallest.test.jkplayer.codec;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;

public class MediaCodecUtils {
    private static final String TAG = "MediaCodecUtils";

    public static void displayDecoders() {
        Log.d(TAG, "displayDecoders()");
        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);//REGULAR_CODECS参考api说明
        MediaCodecInfo[] codecs = list.getCodecInfos();
        Log.d(TAG, "Decoders: ");
        for (MediaCodecInfo codec : codecs) {
            if (codec.isEncoder())
                continue;
            Log.d(TAG, codec.getName());
        }
        Log.d(TAG, "Encoders: ");
        for (MediaCodecInfo codec : codecs) {
            if (codec.isEncoder())
                Log.d(TAG, codec.getName());
        }
    }
}
