package com.kronos.core.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


fun show(
    view: View,
    error: String,
    textColorRes: Int,
    backgroundColorRes: Int,
    duration: Int
): Snackbar? {
    val snackBar: Snackbar = Snackbar.make(view, error, duration)
    snackBar.setTextColor(ContextCompat.getColor(view.context, textColorRes))
    snackBar.view.setBackgroundColor(ContextCompat.getColor(view.context, backgroundColorRes))
    val snackTextView: TextView = snackBar.view.findViewById(com.google.android.material.R.id.snackbar_text)
    snackTextView.maxLines = 4
    snackBar.show()
    return snackBar
}

fun show(view: View, msg: String, textColorRes: Int, backgroundColorRes: Int): Snackbar? {
    val snackBar: Snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
    snackBar.setTextColor(ContextCompat.getColor(view.context, textColorRes))
    snackBar.view.setBackgroundColor(
        ContextCompat.getColor(
            view.context,
            backgroundColorRes
        )
    )
    val snackTextView: TextView = snackBar.view.findViewById(com.google.android.material.R.id.snackbar_text)
    snackTextView.maxLines = 4
    snackBar.show()
    return snackBar
}