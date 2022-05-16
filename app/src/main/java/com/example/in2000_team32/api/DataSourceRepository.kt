package com.example.in2000_team32.api

import android.content.Context
import com.google.gson.Gson

/*
 * Responsible for serving weather data to viewModel.
 * Choose between getting data from cache and MET API.
 */

class DataSourceRepository(val context: Context) {
    private val metDataSource = MetDataSource()
    private val locationDataSource = LocationDataSource()
    private val dsSharedPreferences = DataSourceSharedPreferences(context)

    /**
     * Gets data from API or cache.
     *
     * If data is loaded from API, wipe cache and store
     * new data.
     */
    suspend fun getWeatherData(latitude: Double, longitude: Double): MetResponseDto? {
        val hasCache  = false // Is set to true once the app has loaded some data
        val updateIsDue = true

        // Vi hadde ikke tid til å løse caching, men har lagt opp til at det skal være lett
        // å implementere her.
        if (!hasCache || updateIsDue) { // Test if we have to cache
            val response = metDataSource.fetchMetWeatherForecast(latitude, longitude)
            // Save cache
            dsSharedPreferences.writeMetCache(response)
            return response
        }
        return null
    }

    suspend fun getLocationData(latitude : Double, longitude : Double): String? {
        val locationData = locationDataSource.findLocationNameFromLatLong(latitude, longitude)
        if (locationData != null) {
            if(locationData.address?.city != null){
                return locationData.address.city
            }
            else{
                //Print out the location data
                    if(locationData.address?.municipality != null){
                        return locationData.address.municipality
                    }
                    else{
                        return locationData.address?.county
                    }
            }
        }
        return null
    }

    suspend fun getLocationNamesBasedOnString(searchQuery: String): List<NominatimLocationFromString>? {
        return locationDataSource.findLocationNamesFromString(searchQuery)
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
        if(chosenLocation != null){
            return chosenLocation
        }
        return dsSharedPreferences.getChosenLocation()
    }

    //Set chosen location in sharedpreferences to chosen city
    fun setChosenLocation(chosenLocation : ChosenLocation?){
        val gson = Gson()
        val json = gson.toJson(chosenLocation)
        dsSharedPreferences.setLocation(json)
    }

    //Sets users notification preference in sharedpreferences
    fun setNotifPref(b: Boolean) {
        dsSharedPreferences.setNotifPref(b)
    }

    //Gets users notification preference from sharedpreferences
    fun getNotifPref() : Boolean {
        return dsSharedPreferences.getNotifPref()
    }

    // Set temperature unit
    fun toggleTempUnit() {
        dsSharedPreferences.toggleTempUnit()
    }

    // Get temperature unit
    fun getTempUnit() : Boolean {
        return dsSharedPreferences.getTempUnit()
    }

    //Get permissionAskedBefore status
    fun getPermissionAskedBefore() : Boolean {
        return dsSharedPreferences.getPermissionAskedBefore()
    }

    //Set permissionAskedBefore status
    fun setPermissionAskedBefore(b: Boolean) {
        dsSharedPreferences.setPermissionAskedBefore(b)
    }

}