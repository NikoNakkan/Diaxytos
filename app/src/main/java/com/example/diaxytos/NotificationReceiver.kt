package com.example.diaxytos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var numberOfNotifications : Int=0
        numberOfNotifications= intent.getIntExtra("notification_event", 4)
        Log.v("FATE", numberOfNotifications.toString())
    }
}
