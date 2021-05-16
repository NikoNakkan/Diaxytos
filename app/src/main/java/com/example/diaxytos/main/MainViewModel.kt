package com.example.diaxytos.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaxytos.utils.getTimeseries
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(application: Application): AndroidViewModel(application) {

    var token = ""
    var deviceId = ""
    val places = mutableListOf<LatLng?>()

    private fun addPlacesInList(placesIds: MutableList<String>, onSuccess: () -> Unit) {
        val placesClient = Places.createClient(super.getApplication())
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        for (placeId in placesIds) {
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    places.add(response.place.latLng)
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("WeekFragment", "Error while retrieving place.", e)
                }
        }
    }


    fun getPlaces(onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            val placesIds = getTimeseries(token, deviceId)

            for (i in placesIds.size-1 downTo 0){
                if (placesIds[i] == ""){
                    placesIds.removeAt(i)
                }
            }
            withContext(Dispatchers.Main) {
                addPlacesInList(placesIds, onSuccess)
            }
        }
    }

//    private fun getABanchOfPlaces(): List<String>{
//        return listOf(
//            "ChIJgUbEo8cfqokR5lP9_Wh_DaM",
//            "GhIJQWDl0CIeQUARxks3icF8U8A",
//            "EicxMyBNYXJrZXQgU3QsIFdpbG1pbmd0b24sIE5DIDI4NDAxLCBVU0EiGhIYChQKEgnRTo6ixx-qiRHo_bbmkCm7ZRAN",
//            "EicxMyBNYXJrZXQgU3QsIFdpbG1pbmd0b24sIE5DIDI4NDAxLCBVU0E",
//            "IhoSGAoUChIJ0U6OoscfqokR6P225pApu2UQDQ"
//        )
//    }

}