package com.example.in2000_team32.api

data class NominatimLocationAddress(
    val hamlet: String?,
    val municipality: String?,
    val county: String?,
    val postcode: String?,
    val country: String?,
    val country_code: String?,
    val city: String?
)

data class NominatimLocationFromLatLong(
    val place_id: Number?,
    val licence: String?,
    val osm_type: String?,
    val osm_id: Number?,
    val lat: String?,
    val lon: String?,
    val display_name: String?,
    val address: NominatimLocationAddress?,
    val extratags: Extratags?,
    val boundingbox: List<String>?
)

data class Extratags(val ssr: String?)
