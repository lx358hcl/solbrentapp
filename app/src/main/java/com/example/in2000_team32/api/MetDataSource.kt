package com.example.in2000_team32.api

import android.util.Log
import com.example.in2000_team32.R
import com.example.in2000_team32.contextOfApplication
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson

class MetDataSource {
    /**
     * Fetching data from MetApi /locationforecast endpoint
     * @return MetResponseDto object
     */
    suspend fun fetchMetWeatherForecast(latitude : Double, longitude : Double): MetResponseDto? {
        // Change this if we want to run a dummy server where we can control the weather
        val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/"
        val path = "locationforecast/2.0/complete?lat=${latitude.toString()}&lon=${longitude.toString()}"
        val url = baseUrl + path
        val gson = Gson()

        val runWithDummyApi = false // Choose weather to get data from MET or Dummy API

        try {
            val response: MetResponseDto = gson.fromJson(Fuel.get(if (!runWithDummyApi) url else "http://10.0.2.2:1000/weather").awaitString(), MetResponseDto::class.java)

            // Setting UV index message based on UV index
            val msg: String
            when (response.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky.toInt()) {
                0 -> msg = contextOfApplication.getString(R.string.uv_msg_0)
                1 -> msg = contextOfApplication.getString(R.string.uv_msg_1)
                2 -> msg = contextOfApplication.getString(R.string.uv_msg_2)
                3 -> msg = contextOfApplication.getString(R.string.uv_msg_3)
                4 -> msg = contextOfApplication.getString(R.string.uv_msg_4)
                5 -> msg = contextOfApplication.getString(R.string.uv_msg_5)
                6 -> msg = contextOfApplication.getString(R.string.uv_msg_6)
                7 -> msg = contextOfApplication.getString(R.string.uv_msg_7)
                8 -> msg = contextOfApplication.getString(R.string.uv_msg_8)
                9 -> msg = contextOfApplication.getString(R.string.uv_msg_9)
                10 -> msg = contextOfApplication.getString(R.string.uv_msg_10)
                11 -> msg = contextOfApplication.getString(R.string.uv_msg_11)
                else -> {
                    msg = contextOfApplication.getString(R.string.uv_msg_12)
                }
            }
            response.properties.timeseries[0].data.instant.details.weather_msg = msg
            return response

        } catch (exception: Exception) {
            Log.d("fetchMetWeatherForecast", "Something went wrong on API call: [" + exception + "]")
            return null
        }
    }
}