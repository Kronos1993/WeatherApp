package com.kronos.weatherapp.binding_adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kronos.core.extensions.getHour
import com.kronos.core.extensions.isToday
import com.kronos.core.extensions.of
import com.kronos.domian.model.CurrentWeather
import com.kronos.domian.model.DailyForecast
import com.kronos.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("handle_temp")
fun handleTemp(view: TextView, current: CurrentWeather?) = view.run {
    text = String.format(view.context.getString(R.string.temp_celsius, current?.tempC.toString()))
}

@BindingAdapter("handle_temp")
fun handleTemp(view: TextView, temp: Double?) = view.run {
    text = String.format(view.context.getString(R.string.temp_celsius, temp.toString()))
}

@BindingAdapter("handle_temp_feels_like")
fun handleTempFeelsLike(view: TextView, current: CurrentWeather?) = view.run {
    text =
        String.format(view.context.getString(R.string.feels_like_temp_celsius, current?.feelslikeC.toString()))
}

@BindingAdapter("handle_speed")
fun handleSpeed(view: TextView, current: CurrentWeather?) = view.run {
    text = String.format(view.context.getString(R.string.speed_km, current?.windSpeedKph.toString()))
}

@BindingAdapter("handle_humidity")
fun handleHumidity(view: TextView, current: CurrentWeather?) = view.run {
    text = String.format("%.1f%%", current?.humidity)
}

@BindingAdapter("handle_rain_possibility")
fun handleRainPossibility(view: TextView, rainPossibility: Double?) = view.run {
    text = String.format("%.1f%%", rainPossibility)
}

@BindingAdapter("handle_uv")
fun handleUV(view: TextView, uv: Int?) = view.run {
    text = uv.toString()
}

@BindingAdapter("handle_day")
fun handleDay(view: TextView, current: DailyForecast?) = view.run {
    if(current!=null){
        var date = Date().of(current.date)

        val calendar = Calendar.getInstance()
        calendar.time = date

        val today = Calendar.getInstance()
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        ) {
            view.text = "Today"
        } else {
            today.add(Calendar.DAY_OF_YEAR, 1)
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {
                view.text = "Tomorrow"
            } else {
                //val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayOfWeekString = SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
                view.text = dayOfWeekString
            }
        }
    }
}

@BindingAdapter("show_only_hour")
fun showOnlyHour(view: TextView, time: String?) = view.run {
    if(time!=null){
        var date = Date().of(time,true)?.getHour()
        view.text = date
    }
}
