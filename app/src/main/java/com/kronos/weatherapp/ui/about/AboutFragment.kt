package com.kronos.weatherapp.ui.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.setLanguageForApp
import com.kronos.weatherapp.R
import dagger.hilt.android.AndroidEntryPoint
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

@AndroidEntryPoint
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setLanguageForApp(requireContext(),
            PreferencesUtil.getPreference(requireContext(),requireContext().getString(R.string.default_lang_key),requireContext().getString(R.string.default_language_value))!!)
        return AboutPage(requireContext())
            .isRTL(false)
            .setImage(R.drawable.ic_weather_app_icon)
            .setDescription(getString(R.string.app_description))
            .addGroup(getString(R.string.programmer_group))
            .addEmail("mguerral1993@gmail.com", "Email")
            .addGitHub("Kronos1993","GitHub")
            .addGroup(getString(R.string.app_data_group))
            .addItem(Element(getAppVersion(), com.kronos.resources.R.drawable.ic_info))
            .addItem(Element(getCopyRight(), com.kronos.resources.R.drawable.ic_copyright))
            .create()
    }

    private fun getCopyRight(): String {
        return String.format(getString(R.string.copy_right), 2023)
    }


    private fun getAppVersion(): String {
        try {
            val packageInfo = context?.packageManager?.getPackageInfo("com.kronos.weather", 0)
            return packageInfo?.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }


}