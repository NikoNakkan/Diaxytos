package com.example.diaxytos

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diaxytos.utils.MyNotificationListenerService
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)

    private var nReceiver: NotificationReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, MyNotificationListenerService::class.java))
            this.startForegroundService(Intent(this, MyService::class.java))
        } else {

            startService(Intent(this, MyService::class.java))
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            nReceiver = NotificationReceiver()
            val filter = IntentFilter()
            filter.addAction("NOTIFICATION_LISTENER")
            registerReceiver(nReceiver, filter)

        }


        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyAiNKWf8bXDE6jU_FYFHh0eXJKrcmfZf2w")

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME,
            Place.Field.TYPES,
            Place.Field.ID
        )

// Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99
            )
        }
// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {


            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    var mostPossiblePlace : String
                    var maxLikelihood : Double = 0.0
                    var location_type : String
                    var location_id : String
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


    }

    companion object {
        fun startActivity(context: Context){
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

    internal class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var numberOfNotifications : Int=0
            numberOfNotifications= intent.getIntExtra("notification_event", 4)
            Log.v("FATE", numberOfNotifications.toString())
        }
    }

}