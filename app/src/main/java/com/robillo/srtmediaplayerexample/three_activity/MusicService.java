package com.robillo.srtmediaplayerexample.three_activity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by robinkamboj on 30/12/17.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<String> songs;
    //current position
    private int songPosn;

    @Override
    public void onCreate() {
        super.onCreate();

        songPosn = 0;

        player = new MediaPlayer();

        initMusicPlayer();

    }

    public void initMusicPlayer() {

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);

    }

    public void setList(ArrayList<String> songs) {
        this.songs = songs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public class  MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
