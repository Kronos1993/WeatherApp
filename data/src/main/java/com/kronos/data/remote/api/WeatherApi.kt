package com.kronos.data.remote.api

import com.kronos.data.remote.dto.current.CurrentForecastResponseDto
import com.kronos.data.remote.dto.forecast.ForecastResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    fun currentWeatherForecast(@Query("q")q:String,@Query("lang")lang:String, @Query("key")apiKey:String): Call<CurrentForecastResponseDto>


    @GET("forecast.json")
    fun weatherForecast(@Query("q")q:String,@Query("lang")lang:String, @Query("key")apiKey:String,@Query("days")days:Int = 1): Call<ForecastResponseDto>

}

