package com.example.diaxytos.utils

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.diaxytos.MainActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//public const val REQUEST_ID = 314

class UsedTimeAlarmManager(val context: Context) {

    private val alarmManager: AlarmManager?
    private val alarmIntent: PendingIntent?

    init {
        alarmManager = getAlarmManagerOnInit()
        alarmIntent = getAlarmIntentOnInit()
    }

    private fun getAlarmManagerOnInit(): AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager


    private fun getAlarmIntentOnInit(): PendingIntent? {
        val intent = Intent(context, OnAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    fun setTimeCheckingAlarm() {
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + (15 * 60 * 1000),
            15 * 60 *1000,
            alarmIntent
        )
    }

    fun cancelAlarm(){
        alarmManager?.cancel(alarmIntent)
    }

}

class OnAlarmReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val powerManager: PowerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val displayManager : DisplayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val batteryManager : BatteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val deviceInteractive = powerManager.isInteractive
        val displayState: Int = displayManager.getDisplay(0).state
        val batteryLevel: Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val batteryStatus = batteryManager.isCharging
        val activeNotifications = getNotificationCount(context.applicationContext)

        getCurrentPlace(context){ locationConf ->
            val executorService: ExecutorService = Executors.newFixedThreadPool(1)
            executorService.execute {
                try {
                    val prediction = getModelPrediction(
                        deviceInteractive,
                        displayState,
                        locationConf,
                        batteryLevel,
                        batteryStatus,
                        activeNotifications
                    )

                    if (prediction < System.currentTimeMillis()) {
                        context.showTimeExceededNotification()
                    }
                }
                catch (e: Exception){
                    Log.e(OnAlarmReceiver::class.java.simpleName, "Network Exception", e)
                }
            }
        }
    }

    private fun getCurrentPlace(context: Context, callback: (Double) -> Unit){
        val placesClient = Places.createClient(context)
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME,
            Place.Field.TYPES,
            Place.Field.ID
        )
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    var maxLikelihood = 0.0

                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                        if(placeLikelihood.likelihood > maxLikelihood){
                            if(placeLikelihood.likelihood> 0.1){
                                maxLikelihood= placeLikelihood.likelihood
                            }

                        }
                    }
                    callback(maxLikelihood)
                }
                else {
                    callback(0.0)
                }
            }
        }
        else {
            callback(0.0)
        }
    }
}

