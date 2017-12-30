package com.robillo.srtmediaplayerexample.three_activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.robillo.srtmediaplayerexample.R;

import java.util.ArrayList;

/**
 * Created by robinkamboj on 30/12/17.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    @SuppressWarnings("FieldCanBeLocal")
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    public MediaPlayer mMediaPlayer;
    private ArrayList<String> songs;
    private final IBinder musicBind = new MusicBinder();

    //current position
    @SuppressWarnings("FieldCanBeLocal")
    private int songPosn;

    @Override
    public void onCreate() {
        super.onCreate();

        initMusicPlayer();

    }

    public void initMusicPlayer() {

        songPosn = 0;

        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mSession = new MediaSession(this, "sample session");
        mController = new MediaController(this, mSession.getSessionToken());

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        mSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(
                        getResources(),
                        R.mipmap.ic_launcher_round))
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Pink Floyd")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "Dark Side of the Moon")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "The Great Gig in the Sky")
                .build());
        // Indicate you're ready to receive media commands
        mSession.setActive(true);
        // Attach a new Callback to receive MediaSession updates
        mSession.setCallback(new MediaSession.Callback() {

            // Implement your callbacks

        });
        // Indicate you want to receive transport controls via your Callback
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Create a new Notification
        final Notification noti = new Notification.Builder(this)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new Notification.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mSession.getSessionToken())
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(0xFFDB4437)
                // Set the large and small icons
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Notification content information
                .setContentText("Pink Floyd")
                .setContentInfo("Dark Side of the Moon")
                .setContentTitle("The Great Gig in the Sky")
                // Add some playback controls
                .addAction(R.mipmap.ic_launcher_round, "prev", retreivePlaybackAction(1))
                .addAction(R.mipmap.ic_launcher_round, "pause", retreivePlaybackAction(2))
                .addAction(R.mipmap.ic_launcher_round, "next", retreivePlaybackAction(3))
                .build();

        // Do something with your TransportControls
        final MediaController.TransportControls controls = mSession.getController().getTransportControls();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, noti);
    }

    private PendingIntent retreivePlaybackAction(int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this, MusicService.class);
        switch (which) {
            case 1:
                // Play and pause
                action = new Intent(ACTION_PREVIOUS);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks
                action = new Intent(ACTION_PLAY);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent(ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 3, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }

    public void setList(ArrayList<String> songs) {
        this.songs = songs;
    }

    public void playSong() {
        mMediaPlayer.reset();
        //get song
//        Song playSong = songs.get(songPosn); //get id
//        long currSong = playSong.getID(); //set uri
//        Uri trackUri = ContentUris.withAppendedId(
//                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                currSong);

        try{
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songs.get(0)));
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }

    public void setSong(int songIndex){
//        songPosn=songIndex; // do something
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mSession.setActive(false);
        return false;
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
        //start playback
        mp.start();
    }

    @SuppressWarnings("WeakerAccess")
    public class  MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
