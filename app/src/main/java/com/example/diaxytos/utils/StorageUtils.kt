package com.example.diaxytos.utils

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

private const val FIRST_TIME_KEY = "first_time_key"
private const val DEVICE_ID = "device_id"
private const val USER_NUMBER = "user_number"

fun isFirstTime(activity: Activity): Boolean =
    PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        .getBoolean(FIRST_TIME_KEY, true)

fun setFirstTime(activity: Activity) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
    sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply()
}

fun storeUsersDevice(activity: Activity, deviceId: String, userNumber: Int){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
    sharedPreferences.edit().putString(DEVICE_ID, deviceId).apply()
    sharedPreferences.edit().putInt(USER_NUMBER, userNumber).apply()
}

fun getUsersDevice(activity: Activity) =
    PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        .getString(DEVICE_ID, "")
