package com.kronos.domian.model

import java.io.Serializable

data class ForecastDay(
    val forecastDay : List<DailyForecast>
) : Serializable
