package com.example.diaxytos

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.diaxytos.userDetails.UserDetailsActivity
import com.example.diaxytos.utils.isFirstTime
import kotlinx.android.synthetic.main.activity_starting.*


class StartingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)

        startService()
        addStartingImage()
        navigateOnStart()
    }

    private fun startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "SFASFSA: ")

            startForegroundService(Intent(this, ScreenStateChangeReceiver::class.java))
        } else {
            Log.d("TAG", "FASFSAFSA: ")

            startService(Intent(this, ScreenStateChangeReceiver::class.java))
        }
    }

    private fun addStartingImage(){
        Glide.with(this)
            .load(R.drawable.giraffe_log_sign_in)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(200)))
            .into(startingImageView)
    }

    private fun navigateOnStart(){
        Handler().postDelayed({
            if(isFirstTime(this)){
                UserDetailsActivity.startActivity(this)
            }
            else{
                MainActivity.startActivity(this)
            }
        }, 500)
    }

}