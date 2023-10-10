package com.kronos.domian.model.forecast

import com.kronos.domian.model.CurrentWeather
import com.kronos.domian.model.ForecastDay
import com.kronos.domian.model.Location
import java.io.Serializable

data class Forecast(
    val location: Location,
    val current: CurrentWeather,
    val forecast: ForecastDay,
) : Serializable
