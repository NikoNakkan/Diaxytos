package com.example.diaxytos.utils

import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.diaxytos.MainActivity
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class DetectedActivitiesIntentService : IntentService(TAG) {    override fun onCreate() {
    super.onCreate()
}    override fun onHandleIntent(intent: Intent?) {
    val result = ActivityRecognitionResult.extractResult(intent)
    val detectedActivities = result.probableActivities as ArrayList<*>
    for (activity in detectedActivities) {
        broadcastActivity(activity as DetectedActivity)
    }
}    private fun broadcastActivity(activity: DetectedActivity) {
    val intent = Intent(MainActivity.toString())
    intent.putExtra("type", activity.type)
    intent.putExtra("confidence", activity.confidence)
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}    companion object {        protected val TAG = DetectedActivitiesIntentService::class.java.simpleName
}
}// Use the TAG to name the worker thread.