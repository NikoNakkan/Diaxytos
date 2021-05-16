package com.example.diaxytos.utils

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class MyNotificationListenerService : NotificationListenerService() {
    var counter : Int=0
    override fun onCreate() {
        super.onCreate()
        Log.v("Kok","NIKA")
        for (sbn in getActiveNotifications()) {
            counter++

        }
        val i2 = Intent("NOTIFICATION_LISTENER")
        i2.putExtra("notification_event", counter )
        Log.v("Kok","NIKO")
        sendBroadcast(i2)

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



}