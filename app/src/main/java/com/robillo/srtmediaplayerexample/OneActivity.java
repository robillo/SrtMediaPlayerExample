package com.robillo.srtmediaplayerexample;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class OneActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    MediaPlayer mPlayer, mediaPlayer;
    SurfaceHolder surfaceHolder;
    SurfaceView playerSurfaceView;
    TextView tv_subtitle;
    private static final String TAG = "robillo";

//    String videoSrc = Environment.getExternalStorageDirectory().getPath() + "/video.mp4";
//    String subTitleSrc = Environment.getExternalStorageDirectory().getPath() + "/sub.srt";

    String videoSrc;
    String subTitleSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        videoSrc = "android.resource://" + getPackageName() + "/raw/star";
        subTitleSrc = "android.resource://" + getPackageName() + "/raw/star_srt";

        playerSurfaceView = (SurfaceView) findViewById(R.id.player_surface);
        tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);
        surfaceHolder = playerSurfaceView.getHolder();
        surfaceHolder.addCallback(this);

//        mPlayer = MediaPlayer.create(OneActivity.this, R.raw.star);
//        mPlayer.start();
    }

    public void onDestroy() {
//        mPlayer.stop();
        mediaPlayer.stop();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
//            AssetFileDescriptor afd2 = OneActivity.this.getResources().openRawResourceFd(R.raw.star);
//            if (afd2 == null) {
//                Log.e("dckd", "null afd2");
//                return;
//            }
//            afd2.close();
            mediaPlayer.setDataSource(this, Uri.parse(videoSrc));
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepare();

//            AssetFileDescriptor afd = OneActivity.this.getResources().openRawResourceFd(R.raw.star_srt);
//            if (afd == null) {
//                Log.e("dckd", "null afd1");
//                return;
//            }
//            mediaPlayer.addTimedTextSource(subTitleSrc, MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            mediaPlayer.addTimedTextSource(getSubtitleFile(R.raw.star_srt), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
//            afd.close();
            int textTrackIndex = findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mediaPlayer.getTrackInfo());
            if (textTrackIndex >= 0) {
                mediaPlayer.selectTrack(textTrackIndex);
                Log.e("test", "Track Selected!");
            } else {
                Log.e("test", "Cannot find text track!");
            }

            mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(final MediaPlayer mediaPlayer, final TimedText timedText) {
                    if (timedText != null) {
                        int seconds = mediaPlayer.getCurrentPosition() / 1000;
                        int secondsMax = mediaPlayer.getDuration() / 1000;
                        String temp = "[ " + secondsToDuration(seconds) + " ] " + timedText.getText() + "[ "
                                + secondsToDuration(secondsMax) + " ] ";
                        tv_subtitle.setText(temp);
                    }
                    else {
                        Log.e("test", "null timed text");
                    }
                }
            });

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            //do something
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private int findTrackIndexFor(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfo) {
        Log.e("test", "find track index for ...!");
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    private String getSubtitleFile(int resId) {
        String fileName = getResources().getResourceEntryName(resId);
        File subtitleFile = getFileStreamPath(fileName);
        if (subtitleFile.exists()) {
            Log.d(TAG, "Subtitle already exists");
            return subtitleFile.getAbsolutePath();
        }
        Log.d(TAG, "Subtitle does not exists, copy it from res/raw");

        // Copy the file from the res/raw folder to your app folder on the
        // device
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(resId);
            outputStream = new FileOutputStream(subtitleFile, false);
            copyFile(inputStream, outputStream);
            return subtitleFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, outputStream);
        }
        return "";
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }

    // A handy method I use to close all the streams
    private void closeStreams(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable stream : closeables) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    // To display the seconds in the duration format 00:00:00
    @SuppressLint("DefaultLocale")
    public String secondsToDuration(int seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600,
                (seconds % 3600) / 60, (seconds % 60), Locale.US);
    }
}
