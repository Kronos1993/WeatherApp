package com.kronos.data.remote.dto

data class HourDto(
    val time_epoch: Int,
    val time: String,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Int,
    val condition: ConditionDto,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val windchill_c: Double,
    val windchill_f: Double,
    val heatindex_c: Double,
    val heatindex_f: Double,
    val dewpoint_c: Double,
    val dewpoint_f: Double,
    val will_it_rain: Int,
    val chance_of_rain: Double,
    val will_it_snow: Int,
    val chance_of_snow: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val gust_mph: Double,
    val gust_kph: Double,
    val uv: Double,
)
