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
import com.example.diaxytos.utils.MyNotificationListenerService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest


class ScreenStateChangeReceiver : BroadcastReceiver(){
    val notificationCount : Int =0

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onReceive(p0: Context, p1: Intent?) {
        var mostPossiblePlace : String
        var maxLikelihood : Double = 0.0
        var location_type : String
        var location_id : String
        if(p1?.action.equals(Intent.ACTION_SCREEN_OFF)) {
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
                                    Log.v("HAHAAHA", "mpainei")
                                    mostPossiblePlace = placeLikelihood.place.name.toString()
                                    maxLikelihood= placeLikelihood.likelihood
                                    location_type= placeLikelihood.place.types?.get(0).toString()
                                    location_id=placeLikelihood.place.id.toString()
                                    Log.v("HAHAHA", location_id + location_type + mostPossiblePlace)

                                }

                            }
                        }
                    } else {
                        val exception = task.exception
                        if (exception is ApiException) {
                            Log.v("Place", task.exception.toString())
                            Log.e("TAG", "Place not found: ${exception.statusCode.toString()}")
                        }
                    }
                }
            } else {
                Log.d("TAG", "error")
            }


            val pm: PowerManager = p0?.getSystemService(Context.POWER_SERVICE) as PowerManager
            val displayManager :DisplayManager = p0.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val batteryManager : BatteryManager = p0.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val connectivityManager : ConnectivityManager = p0.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         //   val notificationManager :NotificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            val device_interactive = pm.isInteractive

            val displayId: Int = displayManager.getDisplay(0).state
            val tsLong: Long = System.currentTimeMillis() / 1000
            val system_time: String = tsLong.toString()

            val battery_status = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                batteryManager.isCharging
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            val network: Network? = connectivityManager.activeNetwork
            val network_type = connectivityManager.getNetworkCapabilities(network)

//            val notificationCount: String =notificationManager.activeNotifications.toString()////////////////////////////////


            val batLevel:Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)


        }
        else if(p1?.action.equals(Intent.ACTION_SCREEN_ON)) {
            val displayManager: DisplayManager =
                p0?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val displayId: Int = displayManager.getDisplay(0).state
            Log.d("tagg", displayId.toString())
            Log.d("TAG", "ScreenOn")
        }


    }






//    class MyNotificationListenerService : NotificationListenerService() {
//        var counter : Int=0
//        override fun onCreate() {
//            super.onCreate()
//            var counter : Int=0
//            for (sbn in activeNotifications) {
//                counter++
//
//            }
//
//        }
//
//
//        override fun onNotificationPosted(sbn: StatusBarNotification) {}
//
//        override fun onNotificationRemoved(sbn: StatusBarNotification) {}
//
//
//    }

}