package com.example.in2000_team32.api

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences

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
        if (locationData != null) {
            return locationData.address?.city
        }
        return null
    }

}