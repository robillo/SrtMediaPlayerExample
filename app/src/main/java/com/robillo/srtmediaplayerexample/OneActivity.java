package com.robillo.srtmediaplayerexample;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        MediaPlayer mPlayer = MediaPlayer.create(OneActivity.this, R.raw.star);
        mPlayer.start();
    }
}
