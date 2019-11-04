package com.example.track

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class EventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("xxxxx", "some event: ${intent?.action}")
        LocationUtils.startLocate {
            Log.d("xxxx", "address: $it")
        }
    }
}