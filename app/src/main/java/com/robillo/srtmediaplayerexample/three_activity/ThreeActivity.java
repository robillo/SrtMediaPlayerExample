package com.robillo.srtmediaplayerexample.three_activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.robillo.srtmediaplayerexample.R;

import java.util.ArrayList;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private Button mPlayPause;

    ArrayList<String> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        songList.add("http://readrush.in/audio/subtle/subtle_open.mp3");

        mPlayPause = findViewById(R.id.play_pause);
        mPlayPause.setOnClickListener(this);
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;

            Toast.makeText(ThreeActivity.this, "SERVICE CONNECTED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            Toast.makeText(ThreeActivity.this, "SERVICE DISCONNECTED", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(MusicService.Companion.getACTION_PLAY());
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_pause: {
                Toast.makeText(this, "PLAY", Toast.LENGTH_SHORT).show();
                if(musicSrv!=null) musicSrv.playSong();
            }
        }
    }
}
