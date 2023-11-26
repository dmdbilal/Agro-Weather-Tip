package com.dmdbilal.agroweathertip.data.remote

suspend fun main() {
    val apiService = RetrofitClient.create()
    val response = apiService.getForecast(52.52, 13.41, "temperature_2m,relative_humidity_2m")

    println("Temperature: ${response.current.temperature_2m}°C")
    println("Relative Humidity: ${response.current.relative_humidity_2m}%")
}
