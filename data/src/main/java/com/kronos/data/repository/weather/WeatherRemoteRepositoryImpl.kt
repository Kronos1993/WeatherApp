package com.kronos.data.repository.weather

import com.kronos.data.data_source.weather.WeatherRemoteDataSource
import com.kronos.domian.model.Response
import com.kronos.domian.model.current.CurrentForecast
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.WeatherRemoteRepository
import javax.inject.Inject

class WeatherRemoteRepositoryImpl @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource
) : WeatherRemoteRepository {

    override suspend fun getWeatherData(
        city: String,
        lang: String,
        apiKey: String
    ): Response<CurrentForecast> {
        return weatherRemoteDataSource.currentWeatherForecast(city, lang, apiKey)
    }

    override suspend fun getWeatherDataForecast(
        city: String,
        lang: String,
        apiKey: String,
        days: Int
    ): Response<Forecast> {
        return weatherRemoteDataSource.weatherForecast(
            city,
            lang,
            apiKey,
            days
        )
    }

}