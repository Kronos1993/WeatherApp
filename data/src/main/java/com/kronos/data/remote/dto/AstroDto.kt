package com.kronos.data.remote.dto

data class AstroDto(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moon_phase: String,
    val moon_illumination: String,
    val is_moon_up: Int,
    val is_sun_up: Int,
)
