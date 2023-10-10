package com.kronos.weatherapp.binding_adapters

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.kronos.weatherapp.R
import java.util.*

@BindingAdapter("handle_background")
fun handleBackground(view: ConstraintLayout,date:Date) = view.run {
    when (date.hours) {
        in 1..12 -> view.background = ContextCompat.getDrawable(view.context, R.drawable.screen_background_morning)
        in 13..17 -> view.background = ContextCompat.getDrawable(view.context, R.drawable.screen_background_noon)
        else -> view.background = ContextCompat.getDrawable(view.context, R.drawable.screen_background_nigth)
    }
}
