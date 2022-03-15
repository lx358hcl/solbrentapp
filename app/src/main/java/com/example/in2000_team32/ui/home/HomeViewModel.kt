package com.example.in2000_team32.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private var currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private var currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    private val _textHome = MutableLiveData<String>().apply {
        value = "God\nmorgen"
    }
    val textHome: LiveData<String> = _textHome

    private val _textUV = MutableLiveData<String>().apply {
        value = "3 UV"
    }
    val textUV: LiveData<String> = _textUV

    private val _textDate = MutableLiveData<String>().apply {
        value = currentDate
    }
    val textDate: LiveData<String> = _textDate

    private val _textKlokke = MutableLiveData<String>().apply {
        value = currentTime
    }
    val textKlokke: LiveData<String> = _textKlokke

    private val _textLeft = MutableLiveData<String>().apply {
        value = "Left"
    }
    val textLeft: LiveData<String> = _textLeft

    private val _textLeftMid = MutableLiveData<String>().apply {
        value = "LeftMid"
    }
    val textLeftMid: LiveData<String> = _textLeftMid

    private val _textRightMid = MutableLiveData<String>().apply {
        value = "RightMid"
    }
    val textRightMid: LiveData<String> = _textRightMid

    private val _textRight = MutableLiveData<String>().apply {
        value = "Right"
    }
    val textRight: LiveData<String> = _textRight

}