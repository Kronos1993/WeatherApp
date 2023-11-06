package com.kronos.weatherapp.binding_adapters

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter


@BindingAdapter("handle_visibility")
fun handleVisibility(view: View, enable: Boolean) = view.run {
    isVisible = enable
}

@BindingAdapter("handle_visibility_invisible")
fun handleVisibilityInvisible(view: View, enable: Boolean) = view.run {
    visibility = if (enable)View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("handle_visibility")
fun handleVisibility(view: View, text: String?) = view.run {
    isVisible = !text.isNullOrEmpty()
}

