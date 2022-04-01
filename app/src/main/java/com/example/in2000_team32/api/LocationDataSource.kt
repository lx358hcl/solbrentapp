package com.example.in2000_team32.api

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson

//Finds location based on lat-long and finds lat-long from location
class LocationDataSource {
    suspend fun findLocationNameFromLatLong(latitude : Number, longitude : Number) : NominatimLocationFromLatLong? {
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}&zoom=18&addressdetails=1&extratags=1"
        val gson = Gson()

        try {
            val response: NominatimLocationFromLatLong? = gson.fromJson(Fuel.get(url).awaitString(), NominatimLocationFromLatLong::class.java)
            return response
        }
        catch (exception: Exception) {
            return null
        }
    }

    suspend fun findLocationNameFromString(locationName : String) : List<NominatimLocationFromString>? {
        val url = "https://nominatim.openstreetmap.org/search?q=${locationName}&format=json&addressdetails=1"
        val gson = Gson()

        try {
            val response: List<NominatimLocationFromString>? = gson.fromJson(Fuel.get(url).awaitString(), Array<NominatimLocationFromString>::class.java).toList()
            return response
        }catch (exception: Exception) {
            return null
        }
    }


}