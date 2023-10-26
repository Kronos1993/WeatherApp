package com.kronos.core.util

import android.content.Context
import androidx.preference.PreferenceManager

class PreferencesUtil {

    companion object {
        fun getPreference(context: Context, key: String, default: String) =
            PreferenceManager.getDefaultSharedPreferences(context).getString(key, default)

        fun getPreference(context: Context, key: String, default: Boolean) =
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, default)

        fun getPreference(context: Context, key: String, default: Int) =
            PreferenceManager.getDefaultSharedPreferences(context).getInt(key, default)

        fun getPreference(context: Context, key: String, default: Float) =
            PreferenceManager.getDefaultSharedPreferences(context).getFloat(key, default)

        fun getPreference(context: Context, key: String, default: Long) =
            PreferenceManager.getDefaultSharedPreferences(context).getLong(key, default)

        fun setPreference(context: Context, key: String, value: Any) {
            var preference = PreferenceManager.getDefaultSharedPreferences(context)
            with(preference.edit()) {
                if (value is String)
                    putString(key, value)
                else if (value is Boolean)
                    putBoolean(key, value)
                else if (value is Int)
                    putInt(key, value)
                else if (value is Float)
                    putFloat(key, value)
                else if (value is Long)
                    putLong(key, value)
                apply()
            }
        }


    }

}

