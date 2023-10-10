package com.kronos.data.remote.dto

import java.io.Serializable

data class LocationDto(
    val name: String,
    val regionval: String?,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Int,
    val localtime: String,
) : Serializable
