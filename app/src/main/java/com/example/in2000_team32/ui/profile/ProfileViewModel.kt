package com.example.in2000_team32.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class ProfileViewModel : ViewModel() {

    private val _profile = MutableLiveData<String>().apply {
        value = "Min Hudfarge"
    }
    val profileText: LiveData<String> = _profile
}