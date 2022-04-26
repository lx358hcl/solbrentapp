package com.example.in2000_team32.api

// Data classes used to parse Met Api data from /loactionforecast endpoint

data class MetResponseDto(val properties: PropertiesDto)

data class PropertiesDto(val timeseries: List<TimeSeries>)

data class TimeSeries(val time: String, val data: ForecastData)

data class ForecastData(val instant: ForecastDataInstant, val next_1_hours: NextHourDetails)

data class NextHourDetails(val summary: NextHourSummary)

data class NextHourSummary(val symbol_code: String)

data class ForecastDataInstant(val details: ForecastDataInstantDetails)

data class ForecastDataInstantDetails(val air_pressure_at_sea_level: Double,
                                      val air_temperature: Double,
                                      val air_temperature_percentile_10: Double,
                                      val air_temperature_percentile_90: Double,
                                      val cloud_area_fraction: Double,
                                      val cloud_area_fraction_high: Double,
                                      val could_area_fraction_low: Double,
                                      val cloud_area_fraction_medium: Double,
                                      val dew_point_temperature: Double,
                                      val fog_area_fraction: Double,
                                      val relative_humidity: Double,
                                      val ultraviolet_index_clear_sky: Double,
                                      val wind_from_direction: Double,
                                      val wind_speed: Double,
                                      val wind_speed_percentile_10: Double,
                                      val wind_speed_percentile_90: Double,
                                      var weather_msg: String = "") // weather_msg is not from the API