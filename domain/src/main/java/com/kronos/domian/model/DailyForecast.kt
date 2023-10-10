package com.kronos.domian.model

data class DailyForecast(
    val date: String,
    val dateEpoch: Long,
    val day: Day,
    val astro: Astro,
    val hours: List<Hour>,
)
