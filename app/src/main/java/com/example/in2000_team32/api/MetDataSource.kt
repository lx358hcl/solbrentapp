package com.example.in2000_team32.api

import android.util.Log
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

        val runWithDummyApi: Boolean = false // Choose weather to get data from MET or Dummy API

        try {
            val response: MetResponseDto = gson.fromJson(Fuel.get(if (!runWithDummyApi) url else "http://10.0.2.2:1000/weather").awaitString(), MetResponseDto::class.java)
            //val tRes = Fuel.get("http://192.168.1.46:1000/weather").awaitString() // Request to test server

            // Setting UV index message based on UV index
            var msg: String
            when (response.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky.toInt()) {
                // TODO: Flytt disse tekst strengene til en XML fil
                0 -> msg = "ingen stråling"
                1 -> msg = "lav stråling"
                2 -> msg = "lav stråling"
                3 -> msg = "moderat stråling"
                4 -> msg = "moderat stråling"
                5 -> msg = "moderat stråling"
                6 -> msg = "sterk stråling"
                7 -> msg = "sterk stråling"
                8 -> msg = "svært sterk stråling"
                9 -> msg = "svært sterk stråling"
                10 -> msg = "svært sterk stråling"
                11 -> msg = "ekstrem stråling!"
                else -> {
                    msg = "Feil: Ugyldig eller ingen UV"
                }
            }
            response.properties.timeseries[0].data.instant.details.weather_msg = msg

            println("-------------------")
            println(response) // Actual server
            //println(tRes) // Test server
            println("-------------------")

            return response

        } catch (exception: Exception) {
            Log.d("fetchMetWeatherForecast", "Something went wrong on API call: [" + exception + "]")

            return null
        }
    }
}