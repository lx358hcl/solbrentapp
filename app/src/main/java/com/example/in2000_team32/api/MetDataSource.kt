package com.example.in2000_team32.api

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson


class MetDataSource {

    /**
     * Fetching data from MetApi /locationforecast endpoint
     * @return ??? (TODO: Agree on data structure of return values)
     * -> My thoughs: we simply return the response / or null (on error) and handle formating and extracting data in the view?
     * --> What do we save in the repository?
     */
    suspend fun fetchMetWeatherForecast(): MetResponseDto? {
        // Change this if we want to run a dummy server where we can control the weather
        val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/"
        val path = "locationforecast/2.0/complete?lat=59.911491&lon=10.757933"
        val url = baseUrl + path
        val gson = Gson()

        try {
            val response: MetResponseDto = gson.fromJson(Fuel.get(url).awaitString(), MetResponseDto::class.java)
            //val tRes = Fuel.get("http://192.168.1.46:1000/weather").awaitString() // Request to test server

            // Setting UV index message based on UV index
            var msg: String
            when (response.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky.toInt()) {
                0 -> msg = "Ingen stråling"
                1 -> msg = "Ubetydelig stråling"
                2 -> msg = "Noe stråling"
                3 -> msg = "Noe stråling"
                4 -> msg = "Litt stråling"
                5 -> msg = "Litt stråling"
                6 -> msg = "Endel stråling"
                7 -> msg = "Mye stråling"
                8 -> msg = "Mye stråling"
                9 -> msg = "Veldig mye stråling"
                10 -> msg = "Ekstrem stråling"
                11 -> msg = "Ekstrem stårling!"
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