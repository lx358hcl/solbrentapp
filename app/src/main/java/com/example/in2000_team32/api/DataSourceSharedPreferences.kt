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

    fun writeFitzType(f: Int) {

        val prefsEditor: SharedPreferences.Editor = sharedPref.edit()

        prefsEditor.putString("fitzType", f.toString())
        prefsEditor.commit()
    }

    // Returns 0 if not found
    fun getFitzType() : Int {
        return sharedPref.getString("fitzType", "0")?.toInt() ?: 0
    }

    //Write chosen city to sharedpreferences under "city"
    fun setLocation(location: String?) {
        with(profilSharedPref.edit()){
            putString("location", location)
            apply()
        }
    }

    //Get chosen city from sharedpreferences. Return null if not found
    fun getChosenLocation() : ChosenLocation? {
        val gson: Gson = Gson()
        val json: String? = profilSharedPref.getString("location", "")
        val chosenLocation: ChosenLocation? = gson.fromJson(json, ChosenLocation::class.java)
        return chosenLocation
    }


    //Get theme mode
    fun getThemeMode() : String? {
        return profilSharedPref.getString("theme", null)
    }

    //Write theme mode to sharedpreferences under "theme"
    fun setThemeMode(theme: String) {
        with(profilSharedPref.edit()){
            putString("theme", theme)
            apply()
        }
    }

    //Returns true if not changed
    fun getNotifPref() : Boolean {
        return profilSharedPref.getBoolean("notif", true)
    }

    //Sets users notification preferences
    fun setNotifPref(notifPref: Boolean) {
        with(profilSharedPref.edit()){
            putBoolean("notif", notifPref)
            apply()
        }
    }
}