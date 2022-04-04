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
    suspend fun fetchMetWeatherForecast(latitude : Double, longitude : Double): MetResponseDto? {
        // Change this if we want to run a dummy server where we can control the weather
        val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/"
        val path = "locationforecast/2.0/complete?lat=${latitude.toString()}&lon=${longitude.toString()}"
        val url = baseUrl + path
        val gson = Gson()

        try {
            val response: MetResponseDto = gson.fromJson(Fuel.get(url).awaitString(), MetResponseDto::class.java)
            //val tRes = Fuel.get("http://192.168.1.46:1000/weather").awaitString() // Request to test server

            // Setting UV index message based on UV index
            var msg: String
            when (response.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky.toInt()) {
                0 -> msg = "Ingen UV stråling"
                1 -> msg = "Ubetydelig UV stråling"
                2 -> msg = "Noe UV stråling"
                3 -> msg = "Noe UV stråling"
                4 -> msg = "Litt UV stråling"
                5 -> msg = "Litt UV stråling"
                6 -> msg = "Endel UV stråling"
                7 -> msg = "Mye UV stråling"
                8 -> msg = "Mye UV stråling"
                9 -> msg = "Veldig mye UV stråling"
                10 -> msg = "Ekstrem UV stråling"
                11 -> msg = "Ekstrem UV stårling!"
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