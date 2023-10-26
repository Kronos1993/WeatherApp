package com.kronos.core.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun setLanguageForApp(context:Context,languageToLoad: String) {
    val locale: Locale = if (languageToLoad == "not-set") { //use any value for default
        Locale.getDefault()
    } else {
        Locale(languageToLoad)
    }
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    context.resources.updateConfiguration(
        config,
        context.resources.displayMetrics
    )
}