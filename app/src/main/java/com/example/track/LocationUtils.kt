package com.example.track

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils

typealias LocationListener = (String) -> Unit

object LocationUtils : AMapLocationListener {
    private const val notificationChannelName = "BackgroundLocation"
    private var isCreateChannel = false
    private var locationListener: LocationListener? = null

    private val locationOption: AMapLocationClientOption by lazy {
        getDefaultOption()
    }

    private val locationClient: AMapLocationClient by lazy {
        AMapLocationClient(Utils.getApp()).also {
            it.setLocationOption(locationOption)
            it.setLocationListener(this)
        }
    }

    fun startLocate(listener: LocationListener?) {
        try {
            locationListener = listener
            locationClient.startLocation()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun stopLocate() {
        try {
            locationClient.stopLocation()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun enableBackgroundLocation(enable: Boolean) {
        if (enable) {
            locationClient.enableBackgroundLocation(2001, buildNotification(Utils.getApp()))
        } else {
            locationClient.disableBackgroundLocation(true)
        }
    }

    @SuppressLint("NewApi")
    private fun buildNotification(context: Context): Notification {

        var builder: Notification.Builder? = null
        var notification: Notification? = null
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏

            val channelId = AppUtils.getAppPackageName()
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true)//是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
                val notificationMgr: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationMgr.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(context.applicationContext, channelId)
        } else {
            builder = Notification.Builder(context.applicationContext)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(AppUtils.getAppName())
            .setContentText("正在后台运行")
            .setWhen(System.currentTimeMillis())

        notification = builder.build()
        return notification
    }

    private fun getDefaultOption(): AMapLocationClientOption {
        val mOption = AMapLocationClientOption()
        mOption.locationMode =
            AMapLocationClientOption.AMapLocationMode.Hight_Accuracy//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.isGpsFirst = false//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.httpTimeOut = 30000//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.interval = 2000//可选，设置定位间隔。默认为2秒
        mOption.isNeedAddress = true//可选，设置是否返回逆地理地址信息。默认是true
        mOption.isOnceLocation = false//可选，设置是否单次定位。默认是false
        mOption.isOnceLocationLatest = false//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP)//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.isSensorEnable = false//可选，设置是否使用传感器。默认是false
        mOption.isWifiScan =
            true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        return mOption
    }

    override fun onLocationChanged(location: AMapLocation?) {
        location?.let {
            Log.d("xxxx", "address: $it")
            locationListener?.invoke(getLocationStr(it))
        }
    }

    private fun getLocationStr(location: AMapLocation): String {
        val sb = StringBuilder()
        sb.append(location.address)
        return sb.toString()
    }
}