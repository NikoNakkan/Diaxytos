package com.example.diaxytos

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "SFASFSA: ")

            startForegroundService(Intent(this, ScreenStateChangeReceiver::class.java))
        } else {
            Log.d("TAG", "FASFSAFSA: ")

            startService(Intent(this, ScreenStateChangeReceiver::class.java))
        }

    }
}