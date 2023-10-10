package com.kronos.domian.model.current

import com.kronos.domian.model.CurrentWeather
import com.kronos.domian.model.Location
import java.io.Serializable

class CurrentForecast(
    val location: Location,
    val current: CurrentWeather
):Serializable