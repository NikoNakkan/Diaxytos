package com.example.diaxytos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.diaxytos.utils.MyNotificationListenerService


class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("Broadcast Listened", "Service tried to stop")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, MyService::class.java))
            context.startForegroundService(Intent(context,MyNotificationListenerService::class.java))
        } else {
            context.startService(Intent(context, MyService::class.java))
            context.startService(Intent(context, MyNotificationListenerService::class.java))

        }
    }
}