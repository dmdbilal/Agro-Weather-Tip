package com.dmdbilal.agroweathertip.domain

data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Int,
    val current_units: CurrentUnits,
    val current: CurrentWeather
)

data class CurrentUnits(
    val time: String,
    val interval: String,
    val temperature_2m: String,
    val relative_humidity_2m: String
)

data class CurrentWeather(
    val time: String,
    val interval: Int,
    val temperature_2m: Double,
    val relative_humidity_2m: Int
)


