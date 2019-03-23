package com.smallest.test.jkplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smallest.test.jkplayer.codec.MediaCodecTestActivity;
import com.smallest.test.jkplayer.player.JKPlayerActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mBtnJKPlayer;
    Button mBtnJKCodec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnJKPlayer = (Button) findViewById(R.id.btn_jkplayer);
        mBtnJKCodec = (Button) findViewById(R.id.btn_jkcodec);
        mBtnJKPlayer.setOnClickListener(this);
        mBtnJKCodec.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_jkplayer:
                Intent intentJKPlayer = new Intent(this, JKPlayerActivity.class);
                startActivity(intentJKPlayer);
                break;
            case R.id.btn_jkcodec:
                Intent intentJKCodec = new Intent(this, MediaCodecTestActivity.class);
                startActivity(intentJKCodec);
        }
    }
}
