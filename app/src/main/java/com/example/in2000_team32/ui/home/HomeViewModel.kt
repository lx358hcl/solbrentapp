package com.example.in2000_team32.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.in2000_team32.api.DataSourceRepository
import com.example.in2000_team32.api.NominatimLocationFromString
import com.example.in2000_team32.api.TimeSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class HomeViewModel(application: Application) : AndroidViewModel(application) { // Had to change to AndroidViewModel to be able to get context
    // Connect Data Source Repo. to HomeViewModel
    private val dataSourceRepository = DataSourceRepository(getApplication<Application>().applicationContext)
    private val uvData: MutableLiveData<Double> = MutableLiveData<Double>()

    private val uvDataForecast: MutableLiveData<List<Double>> = MutableLiveData<List<Double>>()
    private val uvStartTimeForecast: MutableLiveData<Int> = MutableLiveData<Int>()
    private val currentTemp: MutableLiveData<Double> = MutableLiveData<Double>()
    private val currentSky: MutableLiveData<String> = MutableLiveData<String>()

    private val weatherMsg: MutableLiveData<String> = MutableLiveData<String>()
    private val locationName : MutableLiveData<String> = MutableLiveData<String>()
    private val places : MutableLiveData<List<NominatimLocationFromString>> = MutableLiveData<List<NominatimLocationFromString>>()
    private var searchJob: Job? = null

    fun getUvData(): LiveData<Double> {
        return uvData
    }

    fun getCurrentTemp(): LiveData<Double> {
        return currentTemp
    }

    fun getCurrentSky(): LiveData<String> {
        return currentSky
    }

    /**
     * @return Current UV data forecast. Sorted list (by time, first forecast is first)
     * of all UV values in forecast
     */
    fun getUvForecastData(): LiveData<List<Double>> {
        return uvDataForecast
    }

    /**
     * @return First hour of UV data forecast
     */
    fun getUvForecastStartTime(): LiveData<Int> {
        return uvStartTimeForecast
    }

    fun getWeatherMsg(): LiveData<String> {
        return weatherMsg
    }

    fun getLocationName() : LiveData<String>{
        return locationName
    }

    fun getPlaces() : LiveData<List<NominatimLocationFromString>>{
        return places
    }

    // Fetch location-area-data
    fun fetchWeatherData(latitude : Double, longitude: Double) {
        // Do an asynchronous operation to fetch users
        viewModelScope.launch(Dispatchers.IO) {
            dataSourceRepository.getWeatherData(latitude, longitude)?.also {
                // Set all live data variables that need to be updated

                // Post
                val uv: Double = it.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky
                uvData.postValue(uv)

                // Post weather msg
                val msg: String = it.properties.timeseries[0].data.instant.details.weather_msg
                weatherMsg.postValue(msg)

                // Post uv forecast
                var uvForecast: MutableList<Double> = mutableListOf()
                for (ts: TimeSeries in it.properties.timeseries) {
                    uvForecast.add(ts.data.instant.details.ultraviolet_index_clear_sky)
                }
                uvDataForecast.postValue(uvForecast)

                // Post start time
                // Set start time variable
                val rawStartTime: String = it.properties.timeseries[0].time
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val date = formatter.parse(rawStartTime.toString())
                val startHour = date.hours
                uvStartTimeForecast.postValue(startHour)

                // Post current temp
                val temp: Double = it.properties.timeseries[0].data.instant.details.air_temperature
                currentTemp.postValue(temp)

                // Post current sky
                val sky: String = it.properties.timeseries[0].data.next_1_hours.summary.symbol_code
                currentSky.postValue(sky)
            }
        }
    }

    // Fetch met-data
    fun fetchLocationData(latitude : Double, longitude : Double) {
        // Do an asynchronous operation to fetch users
        viewModelScope.launch(Dispatchers.IO) {
            dataSourceRepository.getLocationData(latitude, longitude)?.also {
                locationName.postValue(it)
            }
        }
    }


    //Fetch list of places based on string input
    fun fetchPlaces(searchQuery : String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            dataSourceRepository.getLocationNamesBasedOnString(searchQuery) ?.also {
                places.postValue(it)
            }
        }
    }

    //Henter farge fra sharedpreferences
    fun getColor() : Int {
        val returInt: Int
        returInt = dataSourceRepository.getColor()
        return returInt
    }

    //skriver valgt farge til sharedpreferences
    fun writeColor(color : Int) {
        viewModelScope.launch ( Dispatchers.IO ) {
            dataSourceRepository.writeColor(color)
        }
    }

}