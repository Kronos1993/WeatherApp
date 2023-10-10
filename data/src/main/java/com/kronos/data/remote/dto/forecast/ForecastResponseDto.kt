package com.kronos.data.remote.dto.forecast

import com.kronos.data.remote.dto.CurrentWeatherDto
import com.kronos.data.remote.dto.DailyForecastDto
import com.kronos.data.remote.dto.ForecastDayDto
import com.kronos.data.remote.dto.LocationDto
import java.io.Serializable

data class ForecastResponseDto(
    val location: LocationDto,
    val current: CurrentWeatherDto,
    val forecast: ForecastDayDto,
) : Serializable
