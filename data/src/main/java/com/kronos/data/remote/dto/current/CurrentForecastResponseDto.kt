package com.kronos.data.remote.dto.current

import com.kronos.data.remote.dto.CurrentWeatherDto
import com.kronos.data.remote.dto.LocationDto

class CurrentForecastResponseDto(
    val location: LocationDto,
    val current: CurrentWeatherDto
)