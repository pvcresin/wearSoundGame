package com.pvcresin.wearsoundgame

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log

// https://akira-watson.com/android/soundpool.html

class WearActivity : WearableActivity() {
    val TAG = "wear"

    lateinit var mySurfaceView: MySurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear)
        setAmbientEnabled()

        mySurfaceView = findViewById(R.id.mySurfaceView) as MySurfaceView

    }

    override fun onDestroy() {
        mySurfaceView.animate = false
        super.onDestroy()
    }

}
