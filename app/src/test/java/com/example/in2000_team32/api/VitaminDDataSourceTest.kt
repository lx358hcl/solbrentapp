package com.example.in2000_team32.api

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VitaminDDataSourceTest {

    private val vitDDataSource = VitaminDDataSource()
    private lateinit var currentSeason: String


    @BeforeAll
    fun beforeAll(){
        //Season sendes ikke inn some parameter, men er viktig for resultatet.
        //For å ordentlig teste må testen vite hvilken season det er.
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        //var currentSeason = ""

        if (currentMonth in 0..2) {
            currentSeason = "winter"
        } else if (currentMonth in 3..5) {
            currentSeason = "spring"
        } else if (currentMonth in 6..8) {
            currentSeason = "summer"
        } else if (currentMonth in 9..11) {
            currentSeason ="autumn"
        } else {
            currentSeason = "winter"
        }
    }

    @Test
    fun calculateVitaminDUIPerHourForSkinTypeOneTest() {
        val skintype = 1
        val skinTypeMultiplier = 1

        if (currentSeason == "winter"){
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 12000.0 * skinTypeMultiplier)
        } else if (currentSeason == "summer") {
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 7500.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 85000.0 * skinTypeMultiplier)
        } else {
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 1500.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 17150.0 * skinTypeMultiplier)
        }
    }

    @Test
    fun calculateVitaminDUIPerHourForSkinTypeThreeTest() {
        val skintype = 3
        val skinTypeMultiplier = 2

        if (currentSeason == "winter"){
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 12000.0 * skinTypeMultiplier)
        } else if (currentSeason == "summer") {
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 7500.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 85000.0 * skinTypeMultiplier)
        } else {
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 1500.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 17150.0 * skinTypeMultiplier)
        }
    }

    @Test
    fun calculateVitaminDUIPerHourForSkinTypeSixTest() {
        val skintype = 6
        val skinTypeMultiplier = 5

        if (currentSeason == "winter"){
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 12000.0 * skinTypeMultiplier)
        } else if (currentSeason == "summer") {
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 7500.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 85000.0 * skinTypeMultiplier)
        } else {
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1.1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 2)
            assert(result == 1500.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 15)
            assert(result == 17150.0 * skinTypeMultiplier)
        }
    }

    @Test
    fun calculateVitaminDUIPerHourForNorthTest() {
        val skintype = 3
        val skinTypeMultiplier = 2

        if (currentSeason == "winter") {

            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "south", 1)
            assertFalse(result == 400.0 * skinTypeMultiplier)

        } else if (currentSeason == "summer") {

            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "south", 1)
            assertFalse(result == 3000.0 * skinTypeMultiplier)

        } else {
            //Current season betyr ingenting for resultat hvis det er vår eller høst
            //Både nord og sør skal derfor gi samme retur
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "south", 1)
            assert(result == 750.0 * skinTypeMultiplier)

        }

    }

    @Test
    fun calculateVitaminDUIPerHourForSouthTest() {
        val skintype = 3
        val skinTypeMultiplier = 2

        if (currentSeason == "winter") {

            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "south", 1)
            assert(result == 400.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assertFalse(result == 400.0 * skinTypeMultiplier)

        } else if (currentSeason == "summer") {

            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "south", 1)
            assert(result == 3000.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assertFalse(result == 3000.0 * skinTypeMultiplier)

        } else {
            //Current season betyr ingenting for resultat hvis det er vår eller høst
            //Både nord og sør skal derfor gi samme retur
            var result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "south", 1)
            assert(result == 750.0 * skinTypeMultiplier)

            result = vitDDataSource.calculateVitaminDUIPerHour(skintype, "north", 1)
            assert(result == 750.0 * skinTypeMultiplier)

        }


    }

    @Test
    fun calculateTimeTillSunBurnSkinTypeOneTest() {
        var result = vitDDataSource.calculateTimeTillSunBurn(1, 1)
        assert(result == 150.0)

        result = vitDDataSource.calculateTimeTillSunBurn(1, 1.5)
        assert(result == 150.0)

        result = vitDDataSource.calculateTimeTillSunBurn(1, 9)
        assert(result == 25.0)

        result = vitDDataSource.calculateTimeTillSunBurn(1, 15)
        assert(result == 15.0)
    }

    @Test
    fun calculateTimeTillSunBurnSkinTypeThreeTest() {
        var result = vitDDataSource.calculateTimeTillSunBurn(3, 1)
        assert(result == 150.0 * 2)

        result = vitDDataSource.calculateTimeTillSunBurn(3, 1.5)
        assert(result == 150.0 * 2)

        result = vitDDataSource.calculateTimeTillSunBurn(3, 9)
        assert(result == 25.0 * 2)

        result = vitDDataSource.calculateTimeTillSunBurn(3, 15)
        assert(result == 15.0 * 2)
    }

    @Test
    fun calculateTimeTillSunBurnSkinTypeSixTest() {
        var result = vitDDataSource.calculateTimeTillSunBurn(6, 1)
        assert(result == 150.0 * 5)

        result = vitDDataSource.calculateTimeTillSunBurn(6, 1.5)
        assert(result == 150.0 * 5)

        result = vitDDataSource.calculateTimeTillSunBurn(6, 9)
        assert(result == 25.0 * 5)

        result = vitDDataSource.calculateTimeTillSunBurn(6, 15)
        assert(result == 15.0 * 5)
    }
}

