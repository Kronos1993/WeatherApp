package com.kronos.domian.model

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moonPhase: String,
    val moonIllumination: String,
    val isMoonUp: Boolean,
    val isSunUp: Boolean,
)
