package com.robillo.srtmediaplayerexample

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.ybq.android.spinkit.Style
import com.github.ybq.android.spinkit.style.Wave

import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import com.halilibo.bettervideoplayer.subtitle.CaptionsView
import kotlinx.android.synthetic.main.activity_four.*

class FourActivity : AppCompatActivity(), BetterVideoCallback {

    private val TEST_URL = "http://readrush.in/audio/subtle/subtle_open.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_four)

        player.setCallback(this);
        player.setLoadingStyle(2)

        player.setCaptions(Uri.parse("http://readrush.in/subtitles/subtle/subtle_0.srt"), CaptionsView.CMime.SUBRIP)

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(TEST_URL));

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

    }

    override fun onPaused(player: BetterVideoPlayer) {

    }

    override fun onPreparing(player: BetterVideoPlayer) {

    }

    override fun onPrepared(player: BetterVideoPlayer) {

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
