package com.kronos.data.remote.dto

import java.io.Serializable

data class DayDto(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val memp_c: Double,
    val memp_f: Double,
    val avgtemp_c: Double,
    val avgtemp_f: Double,
    val maxwind_mph: Double,
    val maxwind_kph: Double,
    val totalprecip_mm: Double,
    val totalprecip_in: Double,
    val totalsnow_cm: Double,
    val avgvis_km: Double,
    val avgvis_miles: Double,
    val avghumidity: Double,
    val daily_will_it_rain: Int,
    val daily_chance_of_rain: Double,
    val daily_will_it_snow: Int,
    val daily_chance_of_snow: Double,
    val condition: ConditionDto,
    val uv: Int,
) : Serializable
