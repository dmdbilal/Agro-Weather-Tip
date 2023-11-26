package com.dmdbilal.agroweathertip.data.remote

import com.dmdbilal.agroweathertip.domain.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String
    ): ForecastResponse
}