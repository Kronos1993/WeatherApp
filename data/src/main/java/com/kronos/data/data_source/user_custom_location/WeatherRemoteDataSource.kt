package com.kronos.data.data_source.user_custom_location

import com.kronos.domian.model.Response
import com.kronos.domian.model.current.CurrentForecast
import com.kronos.domian.model.forecast.Forecast


interface WeatherRemoteDataSource {
    suspend fun currentWeatherForecast(
        city: String,
        lang: String,
        apiKey: String
    ): Response<CurrentForecast>

    suspend fun weatherForecast(
        city: String,
        lang: String,
        apiKey: String,
        days: Int = 1
    ): Response<Forecast>

    suspend fun weatherForecast(
        lat: Double,
        lon: Double,
        lang: String,
        apiKey: String,
        days: Int = 1
    ): Response<Forecast>

}