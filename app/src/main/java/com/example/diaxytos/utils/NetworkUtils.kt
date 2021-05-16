package com.example.diaxytos.utils

import android.util.Log
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws


private const val BASE_URL = "http://150.140.142.67:8082"
private const val TAG = "NetworkUtils"

@Throws(Exception::class)
fun getToken(): String{
    val client = OkHttpClient()
    val url = "$BASE_URL/api/auth/login"

    val postBody = "{" +
        "\"username\": \"omada0@ceid.upatras.gr\"," +
        "\"password\": \"suropapaniko98!\"" +
    "}"

    val request = Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .post(postBody.toRequestBody())
        .build();

    val response  = client.newCall(request).execute()
    val json = JSONObject(response.body?.string() ?: "")
    return json["token"].toString()
}


@Throws(Exception::class)
fun createDevice(token: String, X: Int, Y: Int): String{
    val client = OkHttpClient()
    val url = "$BASE_URL/api/device"

    val postBody = "{" +
            "\"name\": \"Participant_${X}_$Y\"," +
            "\"type\": \"Participant\"" +
            "}"

    val request = Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .addHeader("X-Authorization", "Bearer $token")
        .post(postBody.toRequestBody())
        .build();

    val response  = client.newCall(request).execute()
    val responseStr = response.body?.string()
    val json = JSONObject(responseStr ?: "")
    return json.getJSONObject("id").getString("id")
}


fun getDeviceToken(deviceId: String, accessToken: String): String{
    val client = OkHttpClient()
    val url = "$BASE_URL/api/device/$deviceId/credentials"

    val request = Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .addHeader("X-Authorization", "Bearer $accessToken")
        .build();

    val response  = client.newCall(request).execute()
    val responseStr = response.body?.string()
    val json = JSONObject(responseStr ?: "")
    return json.getString("credentialsId")
}


fun setDeviceAttributes(deviceToken: String, age: String, gender: String, team: Int): Boolean{
    val client = OkHttpClient()
    val url = "$BASE_URL/api/v1/$deviceToken/attributes"

    val postBody = "{" +
            "\"age\":" + age + "," +
            "\"gender\":" + gender + "," +
            "\"team\": \"$team\"" +
            "}"

    val request = Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .post(postBody.toRequestBody())
        .build();

    val response  = client.newCall(request).execute()
    val responseStr = response.body?.string()
    return responseStr != null
}


fun getCounter(accessToken: String): Int{
    val client = OkHttpClient()
    val deviceId = "eafd63b0-b583-11eb-b9f4-cd7797153d94"
    val deviceToken = getDeviceToken(deviceId, accessToken)

    val url = "$BASE_URL/api/v1/$deviceToken/attributes"

    val getRequest = Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .addHeader("X-Authorization", "Bearer $accessToken")
        .build();

    val response  = client.newCall(getRequest).execute()
    val counter = JSONObject(response.body?.string() ?: "").getJSONObject("client").getInt("counter")


    val postBody = "{" +
            "\"counter\":" + (counter + 1).toString() +
            "}"

    val postRequest = Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .post(postBody.toRequestBody())
        .build();

    client.newCall(postRequest).execute()

    return counter;
}


@Throws(Exception::class)
fun getTimeseries(token: String, deviceId: String): MutableList<String>{
    Log.d("TAG1", "123")
    val client = OkHttpClient()

    val endTs = System.currentTimeMillis()
    val startTs = endTs - 2592000000 // one month before the endTs
    val key = "location_id"
    val url = "$BASE_URL/api/plugins/telemetry/DEVICE/$deviceId/values/timeseries?limit=3000&agg=NONE" +
            "useStrictDataTypes=false&keys=$key&startTs=$startTs&endTs=$endTs"

    val request = Request.Builder()
        .url(url)
        .addHeader("Accept", "application/json")
        .addHeader("X-Authorization", "Bearer $token")
        .build()

    val response  = client.newCall(request).execute()
    val responseStr = response.body?.string()  ?: ""

    return try{
        val jsonArray = JSONObject(responseStr).getJSONArray(key)

        val placesIds = mutableListOf<String>()
        for (i in 0 until jsonArray.length()){
            placesIds.add(jsonArray.getJSONObject(i).getString(key))
        }

        placesIds
    }
    catch (e: JSONException){
        Log.e(TAG, "JSONException while parsing result.", e)
        mutableListOf()
    }
}

fun sendTelemetry(
    deviceId: String,
    token: String,
    device_interactive: String,
    display_state: Int,
    system_time: Long,
    location_type: String,
    location_id: String,
    location_conf: Double,
    battery_level: Int,
    battery_status: String,
    network_type: String,
    notifs_active: Int
){
    val client = OkHttpClient()

    val url = "$BASE_URL/api/plugins/telemetry/DEVICE/$deviceId/timeseries/CLIENT_SCOPE"

    val postBody = "{" +
            "\"device_interactive\": \"$device_interactive\"," +
            "\"display_state\": \"$display_state\"," +
            "\"system_time\": \"$system_time\"," +
//            "\"activity\": \"\"," +
//            "\"activity_conf\": \"\"," +
            "\"location_type\": \"$location_type\"," +
            "\"location_id\": \"$location_id\"," +
            "\"location_conf\": \"$location_conf\"," +
            "\"battery_level\": \"$battery_level\"," +
            "\"battery_status\": \"$battery_status\"," +
            "\"network_type\": \"$network_type\"," +
            "\"notifs_active\": \"$notifs_active\"" +
            "}"

    val request = Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .addHeader("Accept", "*/*")
        .addHeader("X-Authorization", "Bearer $token")
        .post(postBody.toRequestBody())
        .build()

    client.newCall(request).execute()
}