package com.kronos.weatherapp.binding_adapters

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.kronos.weatherapp.R
import java.util.*

@BindingAdapter("handle_background")
fun handleBackground(view: View, date: Date?) = view.run {
    if (date != null) {
        when (date.hours) {
            in 1..12 -> view.background =
                ContextCompat.getDrawable(view.context, R.drawable.screen_background)
            in 13..17 -> view.background =
                ContextCompat.getDrawable(view.context, R.drawable.screen_background)
            else -> view.background =
                ContextCompat.getDrawable(view.context, R.drawable.screen_background)
        }
    }
}
