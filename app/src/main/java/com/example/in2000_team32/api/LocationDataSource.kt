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

    //Finds location-name from string and returns list of possible guesses || function is debounced
    suspend fun findLocationNamesFromString(locationName : String) : List<NominatimLocationFromString>? {
        val url = "https://nominatim.openstreetmap.org/search?q=${locationName}&format=json&addressdetails=1"
        val gson = Gson()

        try {
            var response: List<NominatimLocationFromString>? = gson.fromJson(Fuel.get(url).awaitString(), Array<NominatimLocationFromString>::class.java).toList()
            //Remove duplicates from response list and return list
            response = response?.distinctBy { it.address?.city }
            response = response?.distinctBy { it.address?.town }
            response = response?.distinctBy { it.address?.municipality }

            //Remove values without city name or town or municipality
            response = response?.filter { it.address?.city != null || it.address?.town != null || it.address?.municipality != null }

            return response
        }
        catch (exception: Exception) {
            return null
        }
    }

}