package com.example.track

import android.content.Context
import android.net.Uri
import android.util.Log
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils

object SmsUtils {

    private fun querySms(context: Context, action: (List<SmsData>) -> Unit) {
        try {
            val smsInboxUri = Uri.parse("content://sms/")
            val cr = context.contentResolver;
            val projection = listOf<String>("body", "address")
            val cursor = cr.query(smsInboxUri, projection.toTypedArray(), "", null, null)
            Log.d("xxxxx", "cursor: ${cursor?.count}")
            val smsItems = mutableListOf<SmsData>()
            cursor?.use { c ->
                while (c.moveToNext()) {
                    smsItems += SmsData(body = c.getString(0), address = c.getString(1))
                }
            }
            action(smsItems)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    fun readRecentSms(context: Context, action: (List<SmsData>) -> Unit) {
        if (!PermissionUtils.isGranted(PermissionConstants.SMS)) {
            PermissionUtils.permission(PermissionConstants.SMS)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        querySms(context, action)
                    }

                    override fun onDenied() {
                    }
                }).request()
        } else {
            querySms(context, action)
        }
    }
}

data class SmsData(val body: String, val address: String)