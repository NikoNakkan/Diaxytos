package com.example.diaxytos

import NotificationReceiver
import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.diaxytos.main.MainViewModel
import com.example.diaxytos.utils.MyNotificationListenerService
import com.example.diaxytos.utils.getNotificationsCount
import com.example.diaxytos.utils.toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)

   // private var nReceiver: NotificationReceiver? = null
    private lateinit var map: GoogleMap
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cn = ComponentName(this, MyNotificationListenerService::class.java)
        val flat: String = Settings.Secure.getString(this.contentResolver, "enabled_notification_listeners")
        val enabled = flat.contains(cn.flattenToString())
        if(!enabled)
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

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

        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MainViewModel::class.java)
        viewModel.deviceId = intent.getStringExtra(DEVICE_ID_NAME) ?: return
        viewModel.token = intent.getStringExtra(TOKEN_NAME) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, MyNotificationListenerService::class.java))
            this.startForegroundService(Intent(this, MyService::class.java))
        } else {

            startService(Intent(this, MyService::class.java))
            startService(Intent(this, MyNotificationListenerService::class.java))

        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
//            nReceiver = NotificationReceiver()
//            val filter = IntentFilter()
//            filter.addAction("NOTIFICATION_LISTENER")
//            registerReceiver(nReceiver, filter)
//        }


        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyAiNKWf8bXDE6jU_FYFHh0eXJKrcmfZf2w")

        // Create a new PlacesClient instance
//        val placesClient = Places.createClient(this)
//        // Use fields to define the data types to return.
//        val placeFields: List<Place.Field> = listOf(
//            Place.Field.NAME,
//            Place.Field.TYPES,
//            Place.Field.ID
//        )
//
//// Use the builder to create a FindCurrentPlaceRequest.
//        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
//        val permissionCheck = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            // ask permissions here using below code
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                99
//            )
//        }
//// Call findCurrentPlace and handle the response (first check that the user has granted permission).
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
//            PackageManager.PERMISSION_GRANTED) {
//
//
//            val placeResponse = placesClient.findCurrentPlace(request)
//            placeResponse.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val response = task.result
//                    var mostPossiblePlace : String
//                    var maxLikelihood : Double = 0.0
//                    var location_type : String
//                    var location_id : String
//                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
//                        Log.i(
//                            "TAG",
//                            "Place '${placeLikelihood.place.name}' has likelihood: ${placeLikelihood.likelihood} }"
//                        )
//
//                            if(placeLikelihood.likelihood > maxLikelihood){
//                                if(placeLikelihood.likelihood> 0.1){
//                                    Log.v("HAHAAHA", "mpainei")
//                                    mostPossiblePlace = placeLikelihood.place.name.toString()
//                                    maxLikelihood= placeLikelihood.likelihood
//                                   location_type= placeLikelihood.place.types?.get(0).toString()
//                                    location_id=placeLikelihood.place.id.toString()
//                                    Log.v("HAHAHA", location_id + location_type + mostPossiblePlace)
//
//                                }
//
//                            }
//                    }
//                } else {
//                    val exception = task.exception
//                    if (exception is ApiException) {
//                        Log.v("Place", task.exception.toString())
//                        Log.e("TAG", "Place not found: ${exception.statusCode.toString()}")
//                    }
//                }
//            }
//        } else {
//            Log.d("TAG", "error")
//        }


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(38.246045, 21.735325), 14.0f))
            viewModel.getPlaces {
                val providerBuilder = HeatmapTileProvider.Builder()
                if (viewModel.places.size != 0){
                    for (place in viewModel.places){
                        providerBuilder.data(viewModel.places)
                    }
                }
                map.addTileOverlay(TileOverlayOptions().tileProvider(providerBuilder.build()))
            }
        }

//        Log.d("TAG1", getNotificationsCount(this).toString())
    }


    companion object {
        private const val DEVICE_ID_NAME = "device_id_name"
        private const val TOKEN_NAME = "token_name"
        fun startActivity(context: Context, deviceId: String, token: String){
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(DEVICE_ID_NAME, deviceId)
            intent.putExtra(TOKEN_NAME, token)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}