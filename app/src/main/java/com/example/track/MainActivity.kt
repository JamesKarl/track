package com.example.track

import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        button.setOnClickListener {
            /*SmsUtils.readRecentSms(this) { smsItems ->
                smsItems.forEach {
                    Log.d("xxxx", "$it")
                }
            }*/
            CallLogUtils.readRecentCallLogs(this) {
                it.forEach { log ->
                    Log.d("xxxxx", "$log")
                }
            }
        }
    }
}
