package com.kronos.weatherapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.setLanguageForApp
import com.kronos.weatherapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setLanguageForApp(requireContext(),
            PreferencesUtil.getPreference(requireContext(),requireContext().getString(R.string.default_lang_key),requireContext().getString(R.string.default_language_value))!!)
        setPreferencesFromResource(R.xml.app_settings, rootKey)
    }
}