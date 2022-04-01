package com.example.in2000_team32.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class DataSourceSharedPreferences(val context: Context) {
    private var sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences("metCache", 0)
    }

    /**
     * Function takes in a MetResponseDto, and stores it
     * in shared preferences on android device.
     */
    fun writeMetCache(metResponseDto: MetResponseDto?) {
        // Saving an object in saved preferences
        // https://stackoverflow.com/questions/7145606/how-do-you-save-store-objects-in-sharedpreferences-on-android

        val prefsEditor: SharedPreferences.Editor = sharedPref.edit()
        val gson: Gson = Gson()

        // Convert object to json
        val json: String = gson.toJson(metResponseDto)

        prefsEditor.putString("metResponseDto", json)
        prefsEditor.commit()
    }

    /**
     * Function looks for "metResponseDto" in
     * shared preferences, and returns it if found.
     */
    fun getMetCache(): MetResponseDto? {
        val gson: Gson = Gson()
        val json: String? = sharedPref.getString("metResponseDto", "")
        val metResponseDto: MetResponseDto? = gson.fromJson(json, MetResponseDto::class.java)

        return metResponseDto
    }
}