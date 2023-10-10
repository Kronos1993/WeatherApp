package com.kronos.domian.model

import java.io.Serializable

class CurrentWeather(
    val tempC: Double,
    val isDay: Boolean,
    val condition: Condition,
    val windSpeedKph: Double,
    val windSpeedMph: Double,
    val windDegree: Int,
    val windDir:String,
    val pressureMb: Int,
    val pressureIn: Double,
    val precipitationMm: Double,
    val humidity: Double,
    val cloud: Int,
    val feelslikeC: Double,
    val feelslikeF: Double,
    val visionKM: Int,
    val visionMiles: Int,
    val uv: Int,
    val gustMph: Double,
    val gustKph: Double,
) : Serializable