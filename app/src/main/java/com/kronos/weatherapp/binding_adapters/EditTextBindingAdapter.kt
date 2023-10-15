package com.kronos.weatherapp.binding_adapters

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("error_text")
fun setErrorText(view: TextInputLayout, errorMessageRes: Int) {
    view.run {
        if (errorMessageRes == 0) {
            view.error = null
            view.isErrorEnabled = false
            return@run
        }

        val errorMessage = context.getString(errorMessageRes)
        if (errorMessage.isEmpty()) {
            view.error = null
            view.isErrorEnabled = false
        } else {
            view.error = errorMessage
            view.isErrorEnabled = true

            if (view.endIconDrawable != null) {
                view.errorIconDrawable = null
            }
        }
    }
}

@BindingAdapter("error_text")
fun setErrorText(input: TextInputLayout, value: String?) =
    input.run {
        this.isErrorEnabled = value.orEmpty().isNotEmpty()
        this.error = value

        if (this.endIconDrawable != null) {
            this.errorIconDrawable = null
        }
    }

