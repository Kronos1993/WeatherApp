package com.kronos.domian.model

import java.io.Serializable

class CurrentWeather(
    val tempC: Double = 0.0,
    val isDay: Boolean = false,
    val condition: Condition = Condition(),
    val windSpeedKph: Double = 0.0,
    val windSpeedMph: Double = 0.0,
    val windDegree: Int = -1,
    val windDir:String = "",
    val pressureMb: Int = -1,
    val pressureIn: Double = 0.0,
    val precipitationMm: Double = 0.0,
    val humidity: Double = 0.0,
    val cloud: Int = -1,
    val feelslikeC: Double = 0.0,
    val feelslikeF: Double = 0.0,
    val visionKM: Int = -1,
    val visionMiles: Int = -1,
    val uv: Int = -1,
    val gustMph: Double = 0.0,
    val gustKph: Double = 0.0,
) : Serializable