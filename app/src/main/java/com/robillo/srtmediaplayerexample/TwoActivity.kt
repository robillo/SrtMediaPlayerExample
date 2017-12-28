package com.robillo.srtmediaplayerexample

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_two.*

class TwoActivity : AppCompatActivity(), SurfaceHolder.Callback {

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val TAG = "robillo"
    private var audioSrc : String ?= null
    private var subtitleSrc : String ?= null
    private lateinit var surfaceHolder : SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

        audioSrc = "android.resource://" + getPackageName() + "/raw/star"
        subtitleSrc = "android.resource://" + getPackageName() + "/raw/star_srt"

        surfaceHolder = player_surface.holder
        surfaceHolder.addCallback(this)
    }

}
