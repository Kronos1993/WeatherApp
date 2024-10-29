package com.kronos.weatherapp.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.setLanguageForApp
import com.kronos.weatherapp.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var onSettingChangePublisher: OnSettingChangePublisher

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setLanguageForApp(requireContext(),
            PreferencesUtil.getPreference(requireContext(),requireContext().getString(R.string.default_lang_key),requireContext().getString(R.string.default_language_value))!!)
        setPreferencesFromResource(R.xml.app_settings, rootKey)

        // Configurar listener para el cambio de idioma
        setupLanguagePreferenceChangeListener()
    }

    private fun setupLanguagePreferenceChangeListener() {
        // Encuentra la preferencia de idioma utilizando la clave definida en el XML
        val languagePreference =
            findPreference<ListPreference>(getString(R.string.default_lang_key))

        languagePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val oldValue = PreferencesUtil.getPreference(
                    requireContext(),
                    getString(R.string.default_lang_key),
                    getString(R.string.default_language_value)
                )

                // Notificar a los observadores sobre el cambio de idioma
                onSettingChangePublisher.notifySettingChange(
                    oldValue ?: "",
                    newValue.toString()
                )

                true
            }
    }

}