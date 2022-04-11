package com.example.in2000_team32.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


var getCurrent

class ProfileViewModel : ViewModel() {
    private val _date = MutableLiveData<String>().apply {
        value = currentDate
    }
    val dateText: LiveData<String> = _date
}