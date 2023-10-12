package com.kronos.domian.model

import java.io.Serializable

data class Location(
    val name: String = "",
    val regionval: String? = "",
    val country: String= "",
    val lat: Double= 0.0,
    val lon: Double= 0.0,
    val tzId: String= "",
    val localtimeEpoch: Int= -1,
    val localtime: String= "",
) : Serializable
