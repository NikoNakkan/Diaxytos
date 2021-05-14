package com.example.diaxytos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            this.startForegroundService(Intent(this, MyService::class.java))
        } else {

            startService(Intent(this, MyService::class.java))
        }

        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyAiNKWf8bXDE6jU_FYFHh0eXJKrcmfZf2w")

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)

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
                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                        Log.i(
                            "TAG",
                            "Place '${placeLikelihood.place.name}' has likelihood: ${placeLikelihood.likelihood}"
                        )
                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e("TAG", "Place not found: ${exception.statusCode}")
                    }
                }
            }
        } else {
            Log.d("TAG", "error")
        }


    }
}