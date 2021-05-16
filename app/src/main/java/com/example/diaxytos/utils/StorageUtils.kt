package com.example.diaxytos.utils

import android.app.Activity
import android.app.Service
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

private const val FIRST_TIME_KEY = "first_time_key"
private const val DEVICE_ID = "device_id"
private const val USER_NUMBER = "user_number"
private const val NOTIFICATIONS_COUNT = "notifications_count"

fun isFirstTime(activity: Activity): Boolean =
    PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        .getBoolean(FIRST_TIME_KEY, true)

fun setFirstTime(activity: Activity) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
    sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply()
}

fun getUsersDevice(activity: Activity) =
    PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        .getString(DEVICE_ID, "")

fun storeUsersDevice(activity: Activity, deviceId: String, userNumber: Int){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
    sharedPreferences.edit().putString(DEVICE_ID, deviceId).apply()
    sharedPreferences.edit().putInt(USER_NUMBER, userNumber).apply()
}

fun getNotificationsCount(activity: Activity) =
    PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        .getInt(NOTIFICATIONS_COUNT, 0)

fun getNotificationsCount(service: Service) =
    PreferenceManager.getDefaultSharedPreferences(service.applicationContext)
        .getInt(NOTIFICATIONS_COUNT, 0)

fun setNotificationCount(service: Service, notificationCount: Int) {
    val sharedPreference = PreferenceManager.getDefaultSharedPreferences(service.applicationContext)
    sharedPreference.edit().putInt(NOTIFICATIONS_COUNT, notificationCount).apply()
}
