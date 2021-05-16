package com.example.diaxytos.userDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaxytos.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class UserDetailsViewModel: ViewModel() {

    private val tag = UserDetailsViewModel::class.java.simpleName
    var ageGroup = "18-24"
    var gender = "Male"
    var team = 0
    var counter = 0
    var deviceId = ""
    var token = ""

    fun createUsersDevice(onSuccess: () -> Unit, onFailure: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                token = getToken()
                counter = getCounter(token)
                deviceId = createDevice(token, team, counter)
                val deviceToken = getDeviceToken(deviceId, token)
                val succeed = setDeviceAttributes(deviceToken, ageGroup, gender, team)
                if (succeed){
                    withContext(Dispatchers.Main){
                        onSuccess()
                    }
                }
            }
            catch (e: Exception){
                Log.e(tag, "Error while creating device.", e)
                withContext(Dispatchers.Main){
                    onFailure()
                }
            }
        }
    }
}