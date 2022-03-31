package com.example.in2000_team32.api

data class NominatimLocationFromStringAddress(
    val railway: String?,
    val road: String?,
    val neighbourhood: String?,
    val borough: String?,
    val municipality: String?,
    val city: String?,
    val town: String?,
    val postcode: String?,
    val country: String?,
    val country_code: String?
)

data class NominatimLocationFromString(
    val place_id: Number?,
    val licence: String?,
    val osm_type: String?,
    val osm_id: Number?,
    val boundingbox: List<String>?,
    val lat: String?,
    val lon: String?,
    val display_name: String?,
    val belongsToClass: String?,
    val type: String?,
    val importance: Number?,
    val icon: String?,
    val address: NominatimLocationFromStringAddress?
)
