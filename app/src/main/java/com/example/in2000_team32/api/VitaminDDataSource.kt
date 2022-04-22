package com.example.in2000_team32.api

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import java.util.*

//Finds location based on lat-long and finds lat-long from location
class VitaminDDataSource {
    suspend fun calculateVitaminDProduction(fitztype : Number, hemisphere : String){
        var currentSeason = ""
        var vitaminD = 0.0
        var hemisphere = hemisphere.toString()
        var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        //Find current season based on current month
        if(currentMonth in 0..2){
            currentSeason = if(hemisphere == "north") "winter" else "summer"
        }
        else if(currentMonth in 3..5){
            currentSeason = if (hemisphere == "north") "spring" else "autumn"
        }
        else if(currentMonth in 6..8){
            currentSeason = if(hemisphere == "north") "summer" else "winter"
        }
        else if(currentMonth in 9..11){
            currentSeason = if(hemisphere == "north") "autumn" else "spring"
        }
        else{
            currentSeason = if(hemisphere == "north") "winter" else "summer"
        }

        //Calculate time till sunburn
        var timeTillSunburn = 0.0

        //If person is fair
        if(fitztype in 0..2) {

        }
        //If pair is dark-skinned
        else if(fitztype in 3..5){

        }
        //If person is very-dark-skinned
        else if(fitztype in 6..8){

        }
    }

}