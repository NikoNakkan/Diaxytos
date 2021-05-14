package com.example.diaxytos.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

private const val FIRST_TIME_KEY = "first_time_key"

fun isFirstTime(activity: Activity): Boolean =
    activity.getPreferences(AppCompatActivity.MODE_PRIVATE).getBoolean(FIRST_TIME_KEY, true)

fun setFirstTime(activity: Activity) {
    val sharedPreferences = activity.getPreferences(AppCompatActivity.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply()
}
