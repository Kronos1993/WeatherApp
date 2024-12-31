package com.kronos.weatherapp.ui.settings

interface OnSettingChangePublisher {
    

    fun subscribe(observer: OnSettingChangeObserver)
    fun unSubscribe(observer: OnSettingChangeObserver)
    fun notifySettingChange(oldValue: String, newValue: String)
}