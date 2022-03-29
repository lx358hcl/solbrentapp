package com.example.in2000_team32.api


/**
 * Responsible for serving weather data to viewModel.
 * Choose between getting data from cache and MET API.
 */

class DataSourceRepository {
    private val metDataSource = MetDataSource()

    /**
     * Gets data from API or cache.
     *
     * If data is loaded from API, wipe cache and store
     * new data.
     */
    suspend fun getWeatherData(): MetResponseDto? {
        var hasCache: Boolean = false // Is set to true once the app has loaded some data
        var updateIsDue: Boolean = true

        if (!hasCache || updateIsDue) { // Test if we have to cache
            return metDataSource.fetchMetWeatherForecast()
        }

        // Save cache
        // https://stackoverflow.com/questions/7145606/how-do-you-save-store-objects-in-sharedpreferences-on-android

        return null
    }


}