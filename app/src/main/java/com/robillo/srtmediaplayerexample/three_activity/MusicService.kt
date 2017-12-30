package com.robillo.srtmediaplayerexample.three_activity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast

import com.robillo.srtmediaplayerexample.R
import kotlinx.android.synthetic.main.activity_two.*
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.*
import java.util.*

/**
 * Created by robinkamboj on 30/12/17.
 */

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private var mManager: MediaSessionManager? = null
    private var mSession: MediaSession? = null
    private var mController: MediaController? = null
    lateinit var mMediaPlayer: MediaPlayer
    private var songs: ArrayList<String>? = null
    private val musicBind = MusicBinder()

    //current position
    private var songPosn: Int = 0

    override fun onCreate() {
        super.onCreate()

        initMusicPlayer()

    }

    fun initMusicPlayer() {

        songPosn = 0

        mMediaPlayer = MediaPlayer()

        mMediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mSession = MediaSession(this, "sample session")
        mController = MediaController(this, mSession!!.sessionToken)

        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnErrorListener(this)
        mMediaPlayer.setOnCompletionListener(this)

        mSession!!.setMetadata(MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(
                        resources,
                        R.mipmap.ic_launcher_round))
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Pink Floyd")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "Dark Side of the Moon")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "The Great Gig in the Sky")
                .build())
        // Indicate you're ready to receive media commands
        mSession!!.isActive = true
        // Attach a new Callback to receive MediaSession updates
        mSession!!.setCallback(object : MediaSession.Callback() {

            // Implement your callbacks

        })
        // Indicate you want to receive transport controls via your Callback
        mSession!!.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)

        // Create a new Notification
        val noti = Notification.Builder(this)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(Notification.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mSession!!.sessionToken)
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(-0x24bbc9)
                // Set the large and small icons
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Notification content information
                .setContentText("Pink Floyd")
                .setContentInfo("Dark Side of the Moon")
                .setContentTitle("The Great Gig in the Sky")
                // Add some playback controls
                .addAction(R.mipmap.ic_launcher_round, "prev", retreivePlaybackAction(1))
                .addAction(R.mipmap.ic_launcher_round, "pause", retreivePlaybackAction(2))
                .addAction(R.mipmap.ic_launcher_round, "next", retreivePlaybackAction(3))
                .build()

        // Do something with your TransportControls
        val controls = mSession!!.controller.transportControls

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, noti)

        //MediaPlayer addTimedTextSource
        mMediaPlayer.addTimedTextSource(getSubtitleFile(R.raw.star_srt), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
        var textTrackIndex : Int = -1
        try {
            textTrackIndex = findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mMediaPlayer.trackInfo)
        }
        catch (e : Exception) {
            Toast.makeText(this, "FAILED FIND TEXT TRACK INDEX", Toast.LENGTH_SHORT).show()
        }
        if (textTrackIndex >= 0){
            mMediaPlayer.selectTrack(textTrackIndex)
            Log.e("test", "Track Selected!")
        }
        else {
            Log.e("test", "Cannot find text track!")
        }

        mMediaPlayer.setOnTimedTextListener({ mp, timedText ->
            if(timedText != null){
                val seconds = mp.currentPosition / 1000
                val cd = secondsToDuration(seconds)

                val mmr = FFmpegMediaMetadataRetriever()
                mmr.setDataSource(songs!![0])
                var duration = java.lang.Long.parseLong(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION))
                duration = duration / 1000
                val minute = duration / 60
                val second = duration - minute * 60
                mmr.release()
                val time = minute.toString() + ":" + second.toString()



            }
        })

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    private fun retreivePlaybackAction(which: Int): PendingIntent? {
        val action: Intent
        val pendingIntent: PendingIntent
        val serviceName = ComponentName(this, MusicService::class.java)
        when (which) {
            1 -> {
                // Play and pause
                action = Intent(ACTION_PREVIOUS)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, 1, action, 0)
                return pendingIntent
            }
            2 -> {
                // Skip tracks
                action = Intent(ACTION_PLAY)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, 2, action, 0)
                return pendingIntent
            }
            3 -> {
                // Previous tracks
                action = Intent(ACTION_NEXT)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, 3, action, 0)
                return pendingIntent
            }
            else -> {
            }
        }
        return null
    }

    fun setList(songs: ArrayList<String>) {
        this.songs = songs
    }

    fun playSong() {
        mMediaPlayer.reset()
        //get song
        //        Song playSong = songs.get(songPosn); //get id
        //        long currSong = playSong.getID(); //set uri
        //        Uri trackUri = ContentUris.withAppendedId(
        //                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        //                currSong);

        try {
            mMediaPlayer.setDataSource(applicationContext, Uri.parse(songs!![0]))
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }

        mMediaPlayer.prepareAsync()
    }

    fun setSong(songIndex: Int) {
        //        songPosn=songIndex; // do something
    }

    override fun onBind(intent: Intent): IBinder? {
        return musicBind
    }

    override fun onUnbind(intent: Intent): Boolean {
        mMediaPlayer.stop()
        mMediaPlayer.release()
        mSession!!.isActive = false
        return false
    }

    override fun onCompletion(mp: MediaPlayer) {

    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        //start playback
        mp.start()
    }

    inner class MusicBinder : Binder() {
        fun getService() : MusicService {
            return this@MusicService
        }
    }

    companion object {

        val ACTION_PLAY = "action_play"
        val ACTION_PAUSE = "action_pause"
        val ACTION_REWIND = "action_rewind"
        val ACTION_FAST_FORWARD = "action_fast_foward"
        val ACTION_NEXT = "action_next"
        val ACTION_PREVIOUS = "action_previous"
        val ACTION_STOP = "action_stop"
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
            Log.d("SERVICE", "Subtitle already exists")
            return subtitleFile.absolutePath
        }
        Log.d("SERVICE", "Subtitle does not exists, copy it from res/raw")

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
        } while (length != -1)
    }

    fun closeStreams(vararg closeables: Closeable) {
        for (stream in closeables)
            try {
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
    }

    fun secondsToDuration(seconds: Int): String {
        return String.format("%02d:%02d", (seconds % 3600) / 60, (seconds % 60), Locale.US)
    }
}