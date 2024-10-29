package com.kronos.weatherapp.ui.settings

import javax.inject.Inject

class WeatherOnSettingChangePublisher @Inject constructor(): OnSettingChangePublisher {

    private val observers = mutableListOf<OnSettingChangeObserver>()

    override fun subscribe(observer: OnSettingChangeObserver) {
        observers.add(observer)
    }

    override fun unSubscribe(observer: OnSettingChangeObserver) {
        observers.remove(observer)
    }

    override fun notifySettingChange(oldValue: String, newValue: String) {
        observers.forEach { it.onSettingChange(oldValue, newValue) }
    }
}