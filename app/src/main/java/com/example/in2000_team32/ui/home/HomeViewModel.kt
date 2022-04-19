package com.example.in2000_team32.ui.home

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.app.PendingIntent.getActivity
import android.content.Context
import android.widget.TextView
import androidx.lifecycle.*
import com.example.in2000_team32.api.DataSourceRepository
import com.example.in2000_team32.api.NominatimLocationFromString
import com.example.in2000_team32.api.TimeSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.hours

class HomeViewModel(application: Application) : AndroidViewModel(application) { // Had to change to AndroidViewModel to be able to get context
/*
    private var currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private var currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    private val _textHome = MutableLiveData<String>().apply {
        value = "God\nmorgen"
    }
    val textHome: LiveData<String> = _textHome
 */

    // Connect Data Source Repo. to HomeViewModel
    private val dataSourceRepository = DataSourceRepository(getApplication<Application>().applicationContext)
    private val uvData: MutableLiveData<Double> = MutableLiveData<Double>()

    private val uvDataForecast: MutableLiveData<List<Double>> = MutableLiveData<List<Double>>()
    private val uvStartTimeForecast: MutableLiveData<Int> = MutableLiveData<Int>()

    private val weatherMsg: MutableLiveData<String> = MutableLiveData<String>()
    private val locationName : MutableLiveData<String> = MutableLiveData<String>()
    private val places : MutableLiveData<List<NominatimLocationFromString>> = MutableLiveData<List<NominatimLocationFromString>>()

    /**
     * @return Current UV data. One single Double value.
     */
    fun getUvData(): LiveData<Double> {
        return uvData
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
    @Suppress("DEPRECATION")
    fun fetchWeatherData(latitude : Double, longitude: Double) {
        // Do an asynchronous operation to fetch users
        viewModelScope.launch(Dispatchers.IO) {
            dataSourceRepository.getWeatherData(latitude, longitude)?.also {
                // Set all live data variables that need to be updated
                val uv: Double = it.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky
                uvData.postValue(uv)

                val msg: String = it.properties.timeseries[0].data.instant.details.weather_msg
                weatherMsg.postValue(msg)

                var uvForecast: MutableList<Double> = mutableListOf()
                for (ts: TimeSeries in it.properties.timeseries) {
                    uvForecast.add(ts.data.instant.details.ultraviolet_index_clear_sky)
                }
                uvDataForecast.postValue(uvForecast)

                // Set start time variable
                val rawStartTime: String = it.properties.timeseries[0].time
                val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val date = formatter.parse(rawStartTime.toString())
                val startHour = date.hours.toInt()
                //println("-------------> raw: $rawStartTime, startHour: $startHour")
                uvStartTimeForecast.postValue(startHour)
            }
        }
    }

    // Fetch met-data
    fun fetchLocationData(latitude : Double, longitude : Double) {
        // Do an asynchronous operation to fetch users
        println("SKjera brusjan")
        viewModelScope.launch(Dispatchers.IO) {
            dataSourceRepository.getLocationData(latitude, longitude)?.also {
                println("Her kommer anusen min" + it.toString())
                locationName.postValue(it)
            }
        }
    }

    //Fetch list of places based on string input
    fun fetchPlaces(searchQuery : String){
        viewModelScope.launch(Dispatchers.IO) {
            dataSourceRepository.getLocationNamesBasedOnString(searchQuery) ?.also {
                // Set all live data variables that need to be updated
                println("HERE IT COMES biiiiiiiiitch")
                println(it)
                println("The length of IT is " + it.size)
                places.postValue(it)
            }
        }
    }

}