package com.kronos.weatherapp

import android.app.Application
import com.kronos.logger.exception.ExceptionHandler
import com.kronos.logger.interfaces.ILogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

const val TAG = "WeatherApp"

@HiltAndroidApp
class WeatherApp:Application(){

    @Inject
    lateinit var logger: ILogger
    @Inject
    lateinit var exceptionHandler: ExceptionHandler

    override fun onCreate() {
        super.onCreate()
        try {
            exceptionHandler.init(this)
            logger.configure()
        }catch (e:Exception){
        }

    }
}