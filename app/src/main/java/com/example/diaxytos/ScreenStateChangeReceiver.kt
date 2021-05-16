package com.example.diaxytos
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager

import android.util.Log
import androidx.annotation.RequiresApi
import com.example.diaxytos.utils.MyNotificationListenerService
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition


class ScreenStateChangeReceiver : BroadcastReceiver(){
    val notificationCount : Int =0
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action.equals(Intent.ACTION_SCREEN_OFF)) {
            val pm: PowerManager = p0?.getSystemService(Context.POWER_SERVICE) as PowerManager
            val device_interactive = pm.isInteractive
            val displayManager :DisplayManager = p0.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

            val displayId: Int = displayManager.getDisplay(0).state
            val tsLong: Long = System.currentTimeMillis() / 1000
            val ts: String = tsLong.toString()
            Log.v("TAG", ts)
            val batteryManager : BatteryManager = p0.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val battery_status = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                batteryManager.isCharging
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            val connectivityManager : ConnectivityManager = p0.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network: Network? = connectivityManager.activeNetwork
            val network_type = connectivityManager.getNetworkCapabilities(network)
            Log.v("TAGG", network_type.toString())

            val notificationManager :NotificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val notificationCount: String =notificationManager.activeNotifications.toString()

            val activeNotifications: Array<StatusBarNotification> =
                notificationManager.getActiveNotifications()
            Log.d("TAGGG", activeNotifications.size.toString())

            val batLevel:Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            Log.v("GAGAGA", batLevel.toString())

           // val notifications : Int =MyNotificationListenerService.getNotificationCount()

        }
        else if(p1?.action.equals(Intent.ACTION_SCREEN_ON)) {
            val displayManager: DisplayManager =
                p0?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val displayId: Int = displayManager.getDisplay(0).state
            Log.d("tagg", displayId.toString())
            Log.d("TAG", "ScreenOn")
        }


    }






//    class MyNotificationListenerService : NotificationListenerService() {
//        var counter : Int=0
//        override fun onCreate() {
//            super.onCreate()
//            var counter : Int=0
//            for (sbn in activeNotifications) {
//                counter++
//
//            }
//
//        }
//
//
//        override fun onNotificationPosted(sbn: StatusBarNotification) {}
//
//        override fun onNotificationRemoved(sbn: StatusBarNotification) {}
//
//
//    }

}