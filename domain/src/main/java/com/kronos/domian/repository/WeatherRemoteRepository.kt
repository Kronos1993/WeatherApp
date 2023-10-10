package com.kronos.domian.repository

import com.kronos.domian.model.Response
import com.kronos.domian.model.current.CurrentForecast
import com.kronos.domian.model.forecast.Forecast

interface WeatherRemoteRepository {
    suspend fun getWeatherData(
        city: String,
        lang: String,
        apiKey: String
    ): Response<CurrentForecast>

    suspend fun getWeatherDataForecast(
        city: String,
        lang: String,
        apiKey: String,
        days: Int = 1
    ): Response<Forecast>
}
