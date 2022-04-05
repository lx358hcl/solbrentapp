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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private val weatherMsg: MutableLiveData<String> = MutableLiveData<String>()
    private val locationName : MutableLiveData<String> = MutableLiveData<String>()

    fun getUvData(): LiveData<Double> {
        return uvData
    }

    fun getWeatherMsg(): LiveData<String> {
        return weatherMsg
    }

    fun getLocationName() : LiveData<String>{
        return locationName
    }

    // Fetch location-area-data
    fun fetchWeatherData(latitude : Double, longitude: Double) {
        // Do an asynchronous operation to fetch users
        viewModelScope.launch(Dispatchers.IO) {
            dataSourceRepository.getWeatherData(latitude, longitude)?.also {
                // Set all live data variables that need to be updated
                val uv: Double = it.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky
                uvData.postValue(uv)

                val msg: String = it.properties.timeseries[0].data.instant.details.weather_msg
                weatherMsg.postValue(msg)
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

}