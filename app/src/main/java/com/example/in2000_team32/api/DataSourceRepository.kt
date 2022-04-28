package com.example.in2000_team32.api

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.gson.Gson

/*
 * Responsible for serving weather data to viewModel.
 * Choose between getting data from cache and MET API.
 */

class DataSourceRepository(val context: Context) {
    private val metDataSource = MetDataSource()
    private val locationDataSource = LocationDataSource()
    private val dsSharedPreferences = DataSourceSharedPreferences(context)
    //private val sharedPreferences = getDefaultSharedPreferences

    /**
     * Gets data from API or cache.
     *
     * If data is loaded from API, wipe cache and store
     * new data.
     */
    suspend fun getWeatherData(latitude: Double, longitude: Double): MetResponseDto? {
        var hasCache: Boolean = false // Is set to true once the app has loaded some data
        var updateIsDue: Boolean = true

        if (!hasCache || updateIsDue) { // Test if we have to cache
            val response = metDataSource.fetchMetWeatherForecast(latitude, longitude)
            // Save cache
            dsSharedPreferences.writeMetCache(response)

            return response
        }

        // https://stackoverflow.com/questions/7145606/how-do-you-save-store-objects-in-sharedpreferences-on-android

        return null
    }

    suspend fun getLocationData(latitude : Double, longitude : Double): String? {
        var locationData = locationDataSource.findLocationNameFromLatLong(latitude, longitude)
        println("LOKASJON");
        if (locationData != null) {
            if(locationData.address?.city != null){
                println("This is the city: " + locationData.address?.city)
                return locationData.address?.city
            }
            else{
                //Print out the location data
                    if(locationData.address?.municipality != null){
                        return locationData.address?.municipality
                    }
                    else{
                        return locationData.address?.county
                    }
            }
        }
        return null
    }

    suspend fun getLocationNamesBasedOnString(searchQuery : String): List<NominatimLocationFromString>? {
        var locationData = locationDataSource.findLocationNamesFromString(searchQuery)
        println("Searching from datarepository")
        return locationData
    }

    //Henter farge fra sharedpreferences
    fun writeColor(color : Int){
        dsSharedPreferences.writeSkinColor(color)
    }

    //Henter farge fra sharedpreferences
    fun getColor() : Int{
        return dsSharedPreferences.getSkinColor()
    }

    // Skriver fitztype til sharedpreferences
    fun writeFitzType(f: Int) {
        dsSharedPreferences.writeFitzType(f)
    }

    // Henter fitztype fra sharedpreferences
    fun getFitzType() : Int {
        return dsSharedPreferences.getFitzType()
    }

    //Get chosen city from sharedpreferences
    fun getChosenLocation() : ChosenLocation? {
        //Turn json string into object of type ChosenLocation
        val chosenLocation = dsSharedPreferences.getChosenLocation()
        //If chosenLocation is not null, return city
        if(chosenLocation != null){
            return chosenLocation
        }
        return dsSharedPreferences.getChosenLocation()
    }

    //Set chosen location in sharedpreferences to chosen city
    fun setChosenLocation(chosenLocation : ChosenLocation){
        //Turn object into JSON
        val gson = Gson()
        val json = gson.toJson(chosenLocation)
        dsSharedPreferences.setLocation(json)
    }

}