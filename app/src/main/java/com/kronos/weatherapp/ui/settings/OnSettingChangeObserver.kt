package com.kronos.weatherapp.ui.settings

interface OnSettingChangeObserver {

    fun onSettingChange(oldValue: String, newValue: String)
}