package com.example.diaxytos.utils

import NotificationReceiver
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.diaxytos.MainActivity
import com.example.diaxytos.Restarter
import com.example.diaxytos.ScreenStateChangeReceiver


class MyNotificationListenerService : NotificationListenerService() {

    var counter : Int=0
    private var nReceiver= NotificationReceiver()
    override fun onCreate() {
        super.onCreate()
        registerNotificationReceiver()
        // code taken from https://stackoverflow.com/questions/44425584/context-startforegroundservice-did-not-then-call-service-startforeground#45047542
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(1, notification)
        }

        Log.v("Kok","NIKA")
        for (sbn in getActiveNotifications()) {
            counter++

        }
        val i2 = Intent("NOTIFICATION_LISTENER")
        i2.putExtra("notification_event", counter )
        Log.v("Kok","NIKO")
        sendBroadcast(i2)

    }

    private fun registerNotificationReceiver() {
        nReceiver = NotificationReceiver()
            val filter = IntentFilter()
            filter.addAction("NOTIFICATION_LISTENER")
            registerReceiver(nReceiver, filter)

    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.v("Kok","NIK")
        counter=0
        for (sbn in getActiveNotifications()) {
            counter++

        }
        val i2 = Intent("NOTIFICATION_LISTENER")
        i2.putExtra("notification_event", counter )
        sendBroadcast(i2)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        counter=0
        for (sbn in getActiveNotifications()) {
            Log.v("Kokomba",sbn.toString())
            counter++

        }
        val i2 = Intent("NOTIFICATION_LISTENER")
        i2.putExtra("notification_event", counter )
        sendBroadcast(i2)
    }

    override fun onDestroy() {
        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }




}