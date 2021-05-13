package com.example.diaxytos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.math.log

class ScreenStateChangeReceiver : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action.equals(Intent.ACTION_SCREEN_OFF))
            Log.d("TAG", "ScreenClosed: ")
        else if(p1?.action.equals(Intent.ACTION_SCREEN_ON))
            Log.d("TAG","ScreenOn")

    }


}