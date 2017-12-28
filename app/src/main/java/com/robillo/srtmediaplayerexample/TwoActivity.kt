package com.robillo.srtmediaplayerexample

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import kotlinx.android.synthetic.main.activity_two.*
import java.io.*
import java.util.*

class TwoActivity : AppCompatActivity(), SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    var playing : Boolean = false

    override fun onPrepared(mp: MediaPlayer?) {
        //Set start time, end time (max time)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDisplay(surfaceHolder)
        mediaPlayer.setDataSource(this, Uri.parse(audioSrc))
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.prepare()

        //MediaPlayer addTimedTextSource
        mediaPlayer.addTimedTextSource(getSubtitleFile(R.raw.star_srt), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
        val textTrackIndex = findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mediaPlayer.trackInfo)
        if (textTrackIndex >= 0){
            mediaPlayer.selectTrack(textTrackIndex)
            Log.e("test", "Track Selected!")
        }
        else {
            Log.e("test", "Cannot find text track!")
        }

        mediaPlayer.setOnTimedTextListener({ mp, timedText ->
            if(timedText != null){
                val seconds = mp.currentPosition / 1000
                val secondsMax = mp.duration / 1000
                val cd = "[ " + secondsToDuration(seconds) + " ] "
                val md = "[ " + secondsToDuration(secondsMax) + " ] "

                tv_current_duration.text = cd
                tv_max_duration.text = md

                tv_subtitle.text = timedText.text
            }
        })

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    private val TAG = "robillo"
    private var audioSrc : String ?= null
    private var subtitleSrc : String ?= null
    private lateinit var surfaceHolder : SurfaceHolder
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

        audioSrc = "android.resource://" + getPackageName() + "/raw/star"
        subtitleSrc = "android.resource://" + getPackageName() + "/raw/star_srt"

        surfaceHolder = player_surface.holder
        surfaceHolder.addCallback(this)

        play_pause.setOnClickListener({
            if(playing)
                mediaPlayer.pause()
            else
                mediaPlayer.start()
        })
    }

    override fun onPause() {
        if(mediaPlayer.isPlaying) mediaPlayer.pause()
        playing = false
        super.onPause()
    }

    override fun onStop() {
        if(mediaPlayer.isPlaying) mediaPlayer.pause()
        playing = false
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if(playing == true && !mediaPlayer.isPlaying) mediaPlayer.start()
    }

    override fun onRestart() {
        super.onRestart()
        if(playing == true && !mediaPlayer.isPlaying) mediaPlayer.start()
    }

    private fun findTrackIndexFor(mediaTrackType: Int, trackInfo: Array<MediaPlayer.TrackInfo>): Int {
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

    fun closeStreams(vararg closeables : Closeable) {
        for (stream in closeables)
            try {
                stream.close()
            }
            catch (e : IOException){
                e.printStackTrace()
            }
    }

    fun secondsToDuration(seconds : Int) : String {
        return String.format("%02d:%02d", (seconds % 3600) / 60, (seconds % 60), Locale.US)
    }
}
