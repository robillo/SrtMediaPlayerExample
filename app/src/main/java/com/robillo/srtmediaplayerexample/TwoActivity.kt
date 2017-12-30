package com.robillo.srtmediaplayerexample

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_two.*
import java.io.*
import java.util.*
import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.widget.Toast
import wseemann.media.FFmpegMediaMetadataRetriever


class TwoActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {

    override fun onSeekComplete(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var isTrackAlreadyPlaying : Boolean = false
    var stopPosition : Int = -1

    override fun onPrepared(mp: MediaPlayer?) {
        //Set start time, end time (max time)
        if(mp!=null){
            mediaPlayer = mp
        }
    }

    private val TAG = "robillo"
    private var audioSrc : String ?= null
    private var subtitleSrc : String ?= null
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

//        audioSrc = "android.resource://" + getPackageName() + "/raw/star"
        audioSrc = "http://readrush.in/audio/subtle/subtle_open.mp3"
        subtitleSrc = "android.resource://" + getPackageName() + "/raw/star_srt"

        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, Uri.parse(audioSrc))
        mediaPlayer.setOnPreparedListener(this)
        try {
            mediaPlayer.prepare()
        }
        catch (e : Exception){

        }

        val mmr = FFmpegMediaMetadataRetriever()
        mmr.setDataSource(audioSrc)
        var duration = java.lang.Long.parseLong(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION))
        duration = duration / 1000
        val minute = duration / 60
        val second = duration - minute * 60
        mmr.release()
        val time = minute.toString() + ":" + second.toString()
        tv_max_duration.text = (time)
        progress_seek_bar.max = duration.toInt()

        initMediaPlayer()

        play_pause.setOnClickListener({
            if(mediaPlayer.isPlaying && isTrackAlreadyPlaying){
                mediaPlayer.pause()
                isTrackAlreadyPlaying = false
            }
            else{
                mediaPlayer.start()
                isTrackAlreadyPlaying = true
            }
        })
    }

    fun initMediaPlayer() {
        Handler().post({
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
                    progress_seek_bar.progress = seconds
                    val cd = secondsToDuration(seconds)

                    tv_current_duration.text = cd
                    tv_subtitle.text = timedText.text

                    val mmr = FFmpegMediaMetadataRetriever()
                    mmr.setDataSource(audioSrc)
                    var duration = java.lang.Long.parseLong(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION))
                    duration = duration / 1000
                    val minute = duration / 60
                    val second = duration - minute * 60
                    mmr.release()
                    val time = minute.toString() + ":" + second.toString()
                    tv_max_duration.text = (time)
                    progress_seek_bar.max = duration.toInt()

                    Toast.makeText(this, timedText.text, Toast.LENGTH_SHORT).show()
                }
            })

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        })
    }

    //called before onPause
    override fun onSaveInstanceState(outState: Bundle?) {
        stopPosition = mediaPlayer.currentPosition
        outState?.putInt("position", stopPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState != null){
            stopPosition = savedInstanceState.getInt("position")
        }
    }

    override fun onPause() {
        super.onPause()
        if(mediaPlayer.isPlaying && isTrackAlreadyPlaying){
            mediaPlayer.pause()
            isTrackAlreadyPlaying = false
        }
    }

    override fun onResume() {
        if(stopPosition!=-1 && !mediaPlayer.isPlaying && !isTrackAlreadyPlaying){
            mediaPlayer.seekTo(stopPosition)
            mediaPlayer.start()
            isTrackAlreadyPlaying = true
        }
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        if(mediaPlayer.isPlaying && isTrackAlreadyPlaying){
            mediaPlayer.pause()
            isTrackAlreadyPlaying = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
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
