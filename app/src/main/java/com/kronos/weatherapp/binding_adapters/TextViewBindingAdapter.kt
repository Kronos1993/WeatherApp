package com.kronos.weatherapp.binding_adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kronos.core.extensions.capitalizeFirstLetter
import com.kronos.core.extensions.getHour
import com.kronos.core.extensions.of
import com.kronos.core.util.PreferencesUtil
import com.kronos.domian.model.CurrentWeather
import com.kronos.domian.model.DailyForecast
import com.kronos.domian.model.Location
import com.kronos.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@BindingAdapter("handle_location_name")
fun handleLocationName(view: TextView, current: Location?) = view.run {
    if (current!=null)
        text = context.getString(R.string.location_name, current.country, current.name)
}


@BindingAdapter("handle_temp")
fun handleTemp(view: TextView, current: CurrentWeather?) = view.run {
    if (current!=null)
        text = String.format(view.context.getString(R.string.temp_celsius, current.tempC.toString()))
}

@BindingAdapter("handle_temp")
fun handleTemp(view: TextView, temp: Double?) = view.run {
    text = String.format(view.context.getString(R.string.temp_celsius, temp.toString()))
}

@BindingAdapter("handle_temp_feels_like")
fun handleTempFeelsLike(view: TextView, current: CurrentWeather?) = view.run {
    if (current!=null)
        text = String.format(view.context.getString(R.string.feels_like_temp_celsius, current.feelslikeC.toString()))
}

@BindingAdapter("handle_speed")
fun handleSpeed(view: TextView, current: CurrentWeather?) = view.run {
    if (current!=null)
        text = String.format(view.context.getString(R.string.speed_km, current.windSpeedKph.toString()))
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

@BindingAdapter("handle_date_hour")
fun showOnlyHour(view: TextView, current: Location?) = view.run {
    if(current!=null){
        val date = Date().of(current.localtime,true)
        val dateFormat = if (PreferencesUtil.getPreference(context,context.getString(R.string.default_lang_key),context.getString(R.string.default_language_value))!! == "en")
            SimpleDateFormat("EEE MMM d | h:mm aa", Locale.US)
        else
            SimpleDateFormat("EEE MMM d | h:mm aa", Locale.getDefault())
        var stringDate = ""
        try{
            stringDate = date?.let { dateFormat.format(it) }.toString()
        }catch (_:Exception){

        }
        view.text = stringDate
    }
}

@BindingAdapter("handle_day")
fun handleDay(view: TextView, current: DailyForecast?) = view.run {
    if(current!=null){
        val date = Date().of(current.date)

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }

        val today = Calendar.getInstance()
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        ) {
            view.text = context.getString(R.string.today)
        } else {
            today.add(Calendar.DAY_OF_YEAR, 1)
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {
                view.text = context.getString(R.string.tomorrow)
            } else {
                //val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayOfWeekString = if (PreferencesUtil.getPreference(context,context.getString(R.string.default_lang_key),context.getString(R.string.default_language_value))!! == "en")
                 SimpleDateFormat("EEEE",Locale.US).format(calendar.time)
                else
                    SimpleDateFormat("EEEE",Locale.getDefault()).format(calendar.time)

                view.text = dayOfWeekString.capitalizeFirstLetter()
            }
        }
    }
}

@BindingAdapter("show_only_hour")
fun showOnlyHour(view: TextView, time: String?) = view.run {
    if(time!=null){
        val date = Date().of(time,true)?.getHour()
        view.text = date
    }
}

@BindingAdapter("handle_text")
fun handleText(view: TextView, text: String?) = view.run {
    if(text!=null){
        view.text = text
    }
}
