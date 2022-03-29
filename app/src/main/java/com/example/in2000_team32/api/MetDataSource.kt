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