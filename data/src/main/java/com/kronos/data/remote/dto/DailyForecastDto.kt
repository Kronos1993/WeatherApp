package com.kronos.data.remote.dto

data class DailyForecastDto(
    val date: String,
    val date_epoch: Long,
    val day: DayDto,
    val astro: AstroDto,
    val hour: List<HourDto>,
)
