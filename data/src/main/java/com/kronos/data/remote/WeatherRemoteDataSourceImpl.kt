package com.kronos.data.remote

import android.util.Log
import com.kronos.data.data_source.weather.WeatherRemoteDataSource
import com.kronos.data.remote.api.WeatherApi
import com.kronos.data.remote.mapper.toCurrentForecast
import com.kronos.data.remote.mapper.toForecast
import com.kronos.domian.model.Response
import com.kronos.domian.model.current.CurrentForecast
import com.kronos.domian.model.forecast.Forecast
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    private val weatherApi: WeatherApi,
) : WeatherRemoteDataSource {

    override suspend fun currentWeatherForecast(
        city: String,
        lang: String,
        apiKey: String
    ): Response<CurrentForecast> {
        var result: Response<CurrentForecast> =
            try {
                weatherApi.currentWeatherForecast(city, lang, apiKey).execute().let {
                    if (it.isSuccessful && it.body() != null) {
                        var response = it.body()!!
                        var currentForecast = response.toCurrentForecast()
                        Response<CurrentForecast>(currentForecast, null, code = it.code())
                    } else {
                        Response<CurrentForecast>(null, null, code = it.code())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Response<CurrentForecast>(null, e, listOf(e.message.toString()), code = 500)
            }
        Log.e(this::javaClass.name, "response: $result")
        return result
    }

    override suspend fun weatherForecast(
        city: String,
        lang: String,
        apiKey: String,
        days: Int
    ): Response<Forecast> {
        var result: Response<Forecast> =
            try {
                weatherApi.weatherForecast(city, lang, apiKey, days).execute().let {
                    if (it.isSuccessful && it.body() != null) {
                        var response = it.body()!!
                        var currentForecast = response.toForecast()
                        Response<Forecast>(currentForecast, null, code = it.code())
                    } else {
                        Response<Forecast>(null, null, code = it.code())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Response<Forecast>(null, e, listOf(e.message.toString()), code = 500)
            }
        Log.e(this::javaClass.name, "response: $result")
        return result
    }

}
