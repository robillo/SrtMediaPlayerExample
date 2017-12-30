package com.robillo.srtmediaplayerexample

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.ybq.android.spinkit.Style
import com.github.ybq.android.spinkit.style.Wave

import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import com.halilibo.bettervideoplayer.BetterVideoProgressCallback
import com.halilibo.bettervideoplayer.subtitle.CaptionsView
import kotlinx.android.synthetic.main.activity_four.*

class FourActivity : AppCompatActivity(), BetterVideoCallback, BetterVideoProgressCallback {

    override fun onVideoProgressUpdate(position: Int, duration: Int) {

    }

    private val TEST_URL = "http://readrush.in/audio/subtle/subtle_open.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_four)

        player.setLoop(false)
        player.setLoadingStyle(2)
        player.disableControls()

        player.setCaptions(Uri.parse("http://readrush.in/subtitles/subtle/subtle_0.srt"), CaptionsView.CMime.SUBRIP)
        player.setSource(Uri.parse(TEST_URL));
        player.setProgressCallback(this)

        player.setCallback(this)

        play_pause.setOnClickListener {
            if(player.isPrepared){
                if(player.isPlaying){
                    player.pause()
                }
                else{
                    player.start()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        player.start()
    }

    public override fun onPause() {
        super.onPause()
        // Make sure the player stops playing if the user presses the home button.
        player.pause()
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }

    override fun onStarted(player: BetterVideoPlayer) {
        play_pause.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_circle_filled_black_24dp))
    }

    override fun onPaused(player: BetterVideoPlayer) {
        play_pause.setImageDrawable(resources.getDrawable(R.drawable.ic_play_circle_filled_black_24dp))
    }

    override fun onPreparing(player: BetterVideoPlayer) {

    }

    override fun onPrepared(player: BetterVideoPlayer) {
        play_pause.visibility = View.VISIBLE
        play_pause.isClickable = true
        play_pause.setImageDrawable(resources.getDrawable(R.drawable.ic_play_circle_filled_black_24dp))
    }

    override fun onBuffering(percent: Int) {

    }

    override fun onError(player: BetterVideoPlayer, e: Exception) {

    }

    override fun onCompletion(player: BetterVideoPlayer) {

    }

    override fun onToggleControls(player: BetterVideoPlayer, isShowing: Boolean) {

    }
}
