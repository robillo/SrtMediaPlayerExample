package com.robillo.srtmediaplayerexample

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.TimedText
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_two.*

import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

class OneActivity : AppCompatActivity(), SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private val TAG = "robillo"
    private var audioSrc : String ?= null
    private var subtitleSrc : String ?= null
    private lateinit var surfaceHolder : SurfaceHolder
    private lateinit var mediaPlayer : MediaPlayer

    //    String videoSrc = Environment.getExternalStorageDirectory().getPath() + "/video.mp4";
    //    String subTitleSrc = Environment.getExternalStorageDirectory().getPath() + "/sub.srt";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

        audioSrc = "android.resource://" + getPackageName() + "/raw/star"
        subtitleSrc = "android.resource://" + getPackageName() + "/raw/star_srt"

        surfaceHolder = player_surface.holder
        surfaceHolder.addCallback(this)
    }

    public override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }

    override fun onPrepared(mp: MediaPlayer) {
        mediaPlayer.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDisplay(surfaceHolder)

            mediaPlayer.setDataSource(this, Uri.parse(audioSrc))
            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.prepare()

            mediaPlayer.addTimedTextSource(getSubtitleFile(R.raw.star_srt), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP)
            val textTrackIndex = findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mediaPlayer.trackInfo)
            if (textTrackIndex >= 0) {
                mediaPlayer.selectTrack(textTrackIndex)
                Log.e("test", "Track Selected!")
            } else {
                Log.e("test", "Cannot find text track!")
            }

            mediaPlayer.setOnTimedTextListener { mediaPlayer, timedText ->
                if (timedText != null) {
                    val seconds = mediaPlayer.currentPosition / 1000
                    val secondsMax = mediaPlayer.duration / 1000
                    val temp = ("[ " + secondsToDuration(seconds) + " ] " + timedText.text + "[ "
                            + secondsToDuration(secondsMax) + " ] ")
                    tv_subtitle.text = temp
                } else {
                    Log.e("test", "null timed text")
                }
            }

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        } catch (e: Exception) {
            //do something
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private fun findTrackIndexFor(mediaTrackType: Int, trackInfo: Array<MediaPlayer.TrackInfo>): Int {
        Log.e("test", "find track index for ...!")
        val index = -1
        for (i in trackInfo.indices) {
            if (trackInfo[i].trackType == mediaTrackType) {
                return i
            }
        }
        return index
    }

    private fun getSubtitleFile(resId: Int): String {
        val fileName = resources.getResourceEntryName(resId)
        val subtitleFile = getFileStreamPath(fileName)
        if (subtitleFile.exists()) {
            Log.d(TAG, "Subtitle already exists")
            return subtitleFile.absolutePath
        }
        Log.d(TAG, "Subtitle does not exists, copy it from res/raw")

        // Copy the file from the res/raw folder to your app folder on the
        // device
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = resources.openRawResource(resId)
            outputStream = FileOutputStream(subtitleFile, false)
            copyFile(inputStream, outputStream)
            return subtitleFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeStreams(inputStream!!, outputStream!!)
        }
        return ""
    }

    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream?, outputStream: OutputStream) {
        val BUFFER_SIZE = 1024
        val buffer = ByteArray(BUFFER_SIZE)
        var length = -1
        do {
            length = inputStream!!.read(buffer)
            outputStream.write(buffer, 0, length)
        }
        while (length!=-1)
    }

    // A handy method I use to close all the streams
    fun closeStreams(vararg closeables : Closeable) {
        for (stream in closeables)
            try {
                stream.close()
            }
            catch (e : IOException){
                e.printStackTrace()
            }
    }

    // To display the seconds in the duration format 00:00:00
    @SuppressLint("DefaultLocale")
    fun secondsToDuration(seconds: Int): String {

        return String.format("%02d:%02d",
                seconds % 3600 / 60, seconds % 60, Locale.US)
    }

    companion object {
        private val TAG = "robillo"
    }
}
