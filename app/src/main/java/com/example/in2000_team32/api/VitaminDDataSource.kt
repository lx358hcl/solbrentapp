package com.example.in2000_team32.api

import java.util.*

//Finds location based on lat-long and finds lat-long from location
class VitaminDDataSource {
    fun calculateVitaminDUIPerHour(fitztype: Number, hemisphere : String, uvindex: Number) : Double {
        var VitaminDUIPerHour = 0.0
        val currentSeason: String
        val hemisphere = hemisphere.toString()
        var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        //Calculate vitamin d production in ug/hour
        //1. Find current season based on current month
        if (currentMonth in 0..2) {
            currentSeason = if (hemisphere == "north") "winter" else "summer"
        } else if (currentMonth in 3..5) {
            currentSeason = if (hemisphere == "north") "spring" else "autumn"
        } else if (currentMonth in 6..8) {
            currentSeason = if (hemisphere == "north") "summer" else "winter"
        } else if (currentMonth in 9..11) {
            currentSeason = if (hemisphere == "north") "autumn" else "spring"
        } else {
            currentSeason = if (hemisphere == "north") "winter" else "summer"
        }

        //Calculate vitamin d production based on season
        if (currentSeason == "winter") {
            //Winter implies only arms and face
            when (uvindex) {
                1 -> VitaminDUIPerHour = 400.0
                2 -> VitaminDUIPerHour = 750.0
                3 -> VitaminDUIPerHour = 1200.0
                4 -> VitaminDUIPerHour = 1580.0
                5 -> VitaminDUIPerHour = 2400.0
                6 -> VitaminDUIPerHour = 3000.0
                7 -> VitaminDUIPerHour = 3530.0
                8 -> VitaminDUIPerHour = 4000.0
                9 -> VitaminDUIPerHour = 4615.0
                10 -> VitaminDUIPerHour = 6000.0
                11 -> VitaminDUIPerHour = 6666.0
                12 -> VitaminDUIPerHour = 7500.0
                13 -> VitaminDUIPerHour = 8570.0
                14 -> VitaminDUIPerHour = 10000.0
                15 -> VitaminDUIPerHour = 12000.0
            }
        } else if (currentSeason == "summer") {
            //Summer implies nakedness
            when (uvindex) {
                1 -> VitaminDUIPerHour = 3000.0
                2 -> VitaminDUIPerHour = 7500.0
                3 -> VitaminDUIPerHour = 12000.0
                4 -> VitaminDUIPerHour = 20000.0
                5 -> VitaminDUIPerHour = 24000.0
                6 -> VitaminDUIPerHour = 30000.0
                7 -> VitaminDUIPerHour = 31500.0
                8 -> VitaminDUIPerHour = 33333.0
                9 -> VitaminDUIPerHour = 40000.0
                10 -> VitaminDUIPerHour = 46000.0
                11 -> VitaminDUIPerHour = 54500.0
                12 -> VitaminDUIPerHour = 60000.0
                13 -> VitaminDUIPerHour = 66666.0
                14 -> VitaminDUIPerHour = 75000.0
                15 -> VitaminDUIPerHour = 85000.0
            }
        }
        //Autumn og Spring
        else {
            //Spring or summer  implies nakedness
            when (uvindex.toInt()) {
                1 -> VitaminDUIPerHour = 750.0
                2 -> VitaminDUIPerHour = 1500.0
                3 -> VitaminDUIPerHour = 3333.0
                4 -> VitaminDUIPerHour = 4285.0
                5 -> VitaminDUIPerHour = 6666.0
                6 -> VitaminDUIPerHour = 7500.0
                7 -> VitaminDUIPerHour = 10000.0
                8 -> VitaminDUIPerHour = 11000.0
                9 -> VitaminDUIPerHour = 12000.0
                10 -> VitaminDUIPerHour = 13333.0
                11 -> VitaminDUIPerHour = 15000.0
                12 -> VitaminDUIPerHour = 15000.0
                13 -> VitaminDUIPerHour = 15800.0
                14 -> VitaminDUIPerHour = 15800.0
                15 -> VitaminDUIPerHour = 17150.0
            }
        }
        //Change VitaminDUIPerHour based on skin type

        val f: Int = fitztype.toInt()

        if (f in 1..2) {
            VitaminDUIPerHour = VitaminDUIPerHour * 1
        } else if (f in 3..4) {
            VitaminDUIPerHour = VitaminDUIPerHour / 2
        } else if (f in 5..6) {
            VitaminDUIPerHour = VitaminDUIPerHour / 5
        }

        return VitaminDUIPerHour
    }

    fun calculateTimeTillSunBurn(fitztype: Number, uvindex: Number ) : Number {
        //Calculate time till sunburn
        var timeTillSunburn = 0.0

        when (uvindex.toInt()) {
            1 -> timeTillSunburn = 150.0
            2 -> timeTillSunburn = 100.0
            3 -> timeTillSunburn = 70.0
            4 -> timeTillSunburn = 50.0
            5 -> timeTillSunburn = 40.0
            6 -> timeTillSunburn = 38.0
            7 -> timeTillSunburn = 35.0
            8 -> timeTillSunburn = 30.0
            9 -> timeTillSunburn = 25.0
            10 -> timeTillSunburn = 20.0
            11 -> timeTillSunburn = 19.0
            12 -> timeTillSunburn = 18.0
            13 -> timeTillSunburn = 17.0
            14 -> timeTillSunburn = 16.0
            15 -> timeTillSunburn = 15.0
        }

        val f : Int = fitztype.toInt()

        //Check skin type and adjust timeTillSunburn
        if (f in 1..2) {
            timeTillSunburn = timeTillSunburn * 1
        } else if (f in 3..4) {
            timeTillSunburn = timeTillSunburn * 2
        } else if (f in 5..6) {
            timeTillSunburn = timeTillSunburn * 5
        }

        return timeTillSunburn

    }

}