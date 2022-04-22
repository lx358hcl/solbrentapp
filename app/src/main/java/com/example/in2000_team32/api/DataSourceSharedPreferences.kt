package com.example.in2000_team32.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class DataSourceSharedPreferences(val context: Context) {
    private var sharedPref: SharedPreferences
    private var profilSharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences("metCache", 0)
        profilSharedPref = context.getSharedPreferences("profilData", Context.MODE_PRIVATE)
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

    //Skriver valgt farge til sharedpreferences under "skinColor"
    fun writeSkinColor(color: Int) {
        with(profilSharedPref.edit()){
            putInt("skinColor", color)
            apply()
        }
    }

    //Henter skinColor fra sharedpreferences. Gir 0 hvis ikke funnet
    fun getSkinColor() : Int {
        return profilSharedPref.getInt("skinColor", 0)
    }

}