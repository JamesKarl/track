package com.example.track

import android.annotation.SuppressLint
import android.content.Context
import android.provider.CallLog
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import java.util.*

object CallLogUtils {

    fun readRecentCallLogs(context: Context, action: (List<CallLogData>) -> Unit) {
        if (!PermissionUtils.isGranted(PermissionConstants.PHONE)) {
            PermissionUtils.permission(PermissionConstants.PHONE)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        queryCallLogs(context, action)
                    }

                    override fun onDenied() {
                    }
                }).request()
        } else {
            queryCallLogs(context, action)
        }
    }


    @SuppressLint("MissingPermission")
    fun queryCallLogs(context: Context, action: (List<CallLogData>) -> Unit) {
        try {
            val uri = CallLog.Calls.CONTENT_URI
            val projection = listOf<String>("name", "number", "type", "date", "duration")
            val cursor = context.contentResolver.query(
                uri,
                projection.toTypedArray(),
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER
            )
            val logItems = mutableListOf<CallLogData>()
            cursor?.use {
                while (it.moveToNext()) {
                    val status = it.getString(2)
                    logItems += CallLogData(
                        name = it.getString(0) ?: "???",
                        number = it.getString(1),
                        status = when (status) {
                            "1" -> CallStatus.IN
                            "2" -> CallStatus.OUT
                            else -> CallStatus.MISS
                        },
                        date = Date(it.getLong(3)),
                        duration = it.getLong(4)
                    )
                }
            }
            action(logItems)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

data class CallLogData(
    val name: String,
    val number: String,
    val status: CallStatus,
    val date: Date,
    val duration: Long
)

enum class CallStatus(private val status: String) {
    IN("呼入"), OUT("呼出"), MISS("未接");

    override fun toString(): String {
        return status
    }
}