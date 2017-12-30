package com.robillo.srtmediaplayerexample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.robillo.srtmediaplayerexample.three_activity.ThreeActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityOne.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, OneActivity::class.java))
        })

        activityTwo.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, TwoActivity::class.java))
        })

        activityThree.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ThreeActivity::class.java))
        })

        activityFour.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, FourActivity::class.java))
        })
    }
}
