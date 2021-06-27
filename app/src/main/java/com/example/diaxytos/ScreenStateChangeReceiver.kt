package com.example.diaxytos
import android.Manifest
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diaxytos.utils.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max


class ScreenStateChangeReceiver : BroadcastReceiver(){

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onReceive(p0: Context, p1: Intent?) {
        val usedTimeAlarmManager = UsedTimeAlarmManager(p0)

        var mostPossiblePlace : String = ""
        var maxLikelihood : Double = 0.0
        var location_type : String = ""
        var location_id : String = ""
        var location_conf: Double = 0.0


        val pm: PowerManager = p0.getSystemService(Context.POWER_SERVICE) as PowerManager
        val batteryManager : BatteryManager = p0.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val connectivityManager : ConnectivityManager = p0.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //   val notificationManager :NotificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val device_interactive = pm.isInteractive.toString()

        val system_time: Long = System.currentTimeMillis() / 1000
        val notifs_active = getNotificationCount(p0.applicationContext)

        val battery_status = batteryManager.isCharging.toString()
        val network: Network? = connectivityManager.activeNetwork
        var network_type = connectivityManager.getNetworkCapabilities(network).toString()
        network_type = if(network_type.contains("WIFI"))
            "WIFI"
        else if(network_type.contains("CELLULAR"))
            "CELLULAR"
        else
            "OTHER"

        val battery_level: Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        if(p1?.action.equals(Intent.ACTION_SCREEN_OFF)) {
            usedTimeAlarmManager.cancelAlarm()

            val displayManager :DisplayManager = p0.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display_state: Int = displayManager.getDisplay(0).state

            Places.initialize(p0.applicationContext, "AIzaSyAiNKWf8bXDE6jU_FYFHh0eXJKrcmfZf2w")

            // Create a new PlacesClient instance
            val placesClient = Places.createClient(p0)
            // Use fields to define the data types to return.
            val placeFields: List<Place.Field> = listOf(
                    Place.Field.NAME,
                    Place.Field.TYPES,
                    Place.Field.ID
            )

// Use the builder to create a FindCurrentPlaceRequest.
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
            if (ContextCompat.checkSelfPermission(p0, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                val placeResponse = placesClient.findCurrentPlace(request)
                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result
                        for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                            Log.i(
                                    "TAG",
                                    "Place '${placeLikelihood.place.name}' has likelihood: ${placeLikelihood.likelihood} }"
                            )

                            if(placeLikelihood.likelihood > maxLikelihood){
                                if(placeLikelihood.likelihood> 0.1){
                                    mostPossiblePlace = placeLikelihood.place.name.toString()
                                    maxLikelihood= placeLikelihood.likelihood

                                    location_type= placeLikelihood.place.types?.get(0).toString()
                                    location_id=placeLikelihood.place.id.toString()

                                }

                            }
                        }
                        location_conf = maxLikelihood

                    } else {
//        Log.d("123", (SystemClock.elapsedRealtime() + 1000).toString())
//        alarmManager?.setRepeating(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            /*SystemClock.elapsedRealtime() +*/ 5000,
//            AlarmManager.INTERVAL_HALF_HOUR,
//            alarmIntent
//        )
                        val exception = task.exception
                        if (exception is ApiException) {
                            Log.v("Place", task.exception.toString())
                            Log.e("TAG", "Place not found: ${exception.statusCode.toString()}")
                        }
                    }

                    val deviceId = getUsersDevice(p0.applicationContext) ?: ""

                    val executorService: ExecutorService = Executors.newFixedThreadPool(1)
                    executorService.execute {
                        val token = getToken()

                        sendTelemetry(
                            deviceId, token, device_interactive, display_state,
                            system_time, location_type, location_id, location_conf, battery_level,
                            battery_status, network_type, notifs_active
                        )
                    }

//                    request to send data
                }
            } else {
                Log.d("TAG", "error")
            }
        }
        else if(p1?.action.equals(Intent.ACTION_SCREEN_ON)) {
            usedTimeAlarmManager.setTimeCheckingAlarm()

            val displayManager :DisplayManager = p0.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display_state: Int = displayManager.getDisplay(0).state

            Places.initialize(p0.applicationContext, "AIzaSyAiNKWf8bXDE6jU_FYFHh0eXJKrcmfZf2w")

            // Create a new PlacesClient instance
            val placesClient = Places.createClient(p0)
            // Use fields to define the data types to return.
            val placeFields: List<Place.Field> = listOf(
                Place.Field.NAME,
                Place.Field.TYPES,
                Place.Field.ID
            )

// Use the builder to create a FindCurrentPlaceRequest.
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
            if (ContextCompat.checkSelfPermission(p0, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

                val placeResponse = placesClient.findCurrentPlace(request)
                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result
                        for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                            Log.i(
                                "TAG",
                                "Place '${placeLikelihood.place.name}' has likelihood: ${placeLikelihood.likelihood} }"
                            )

                            if(placeLikelihood.likelihood > maxLikelihood){
                                if(placeLikelihood.likelihood> 0.1){
                                    mostPossiblePlace = placeLikelihood.place.name.toString()
                                    maxLikelihood= placeLikelihood.likelihood

                                    location_type= placeLikelihood.place.types?.get(0).toString()
                                    location_id=placeLikelihood.place.id.toString()

                                }

                            }
                        }
                        location_conf = maxLikelihood

                    } else {
                        val exception = task.exception
                        if (exception is ApiException) {
                            Log.v("Place", task.exception.toString())
                            Log.e("TAG", "Place not found: ${exception.statusCode.toString()}")
                        }
                    }

                    val deviceId = getUsersDevice(p0.applicationContext) ?: ""

                    Log.d("TAG1", deviceId)

                    val executorService: ExecutorService = Executors.newFixedThreadPool(1)
                    executorService.execute {
                        val token = getToken()

                        sendTelemetry(
                            deviceId, token, device_interactive, display_state,
                            system_time, location_type, location_id, location_conf, battery_level,
                            battery_status, network_type, notifs_active
                        )

                    }
                }
            } else {
                Log.d("TAG", "error")
            }
        }


    }
}