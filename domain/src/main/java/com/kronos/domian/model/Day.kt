package com.kronos.domian.model

import java.io.Serializable

data class Day(
    val maxtempC: Double,
    val maxtempF: Double,
    val mempC: Double,
    val mempF: Double,
    val avgtempC: Double,
    val avgtempF: Double,
    val maxwindMph: Double,
    val maxwindKph: Double,
    val totalprecipMm: Double,
    val totalprecipIn: Double,
    val totalsnowCm: Double,
    val avgvisKm: Double,
    val avgvisMiles: Double,
    val avghumidity: Double,
    val dailyWillItRain: Boolean,
    val dailyChanceOfRain: Double,
    val dailyWillItSnow: Boolean,
    val dailyChanceOfSnow: Double,
    val condition: Condition,
    val uv: Int,
) : Serializable
