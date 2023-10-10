package com.kronos.domian.model

import java.io.Serializable

data class Location(
    val name: String,
    val regionval: String?,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tzId: String,
    val localtimeEpoch: Int,
    val localtime: String,
) : Serializable
