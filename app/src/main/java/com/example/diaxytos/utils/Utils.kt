package com.example.diaxytos.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.diaxytos.R

//const val CHANNEL_ID = NotificationCompat.CATEGORY_RECOMMENDATION
const val CHANNEL_ID = "time_exceeded_recommendation"
const val CHANNEL_TITLE = "Time Exceeded Recommendations"
const val NOTIFICATION_ID = 0

fun Context.toast(text: Any?) =
    Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()

fun Context.longToast(text: Any?) =
    Toast.makeText(this, text.toString(), Toast.LENGTH_LONG).show()

fun Context.showTimeExceededNotification(){
    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Average time exceeded")
        .setContentText("You might have exceeded your average time of use, maybe you should take a break.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_TITLE, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        builder.setChannelId(CHANNEL_ID)
    }

    notificationManager.notify(NOTIFICATION_ID, builder.build())
}