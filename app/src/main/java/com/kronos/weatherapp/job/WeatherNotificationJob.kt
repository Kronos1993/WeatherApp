package com.kronos.weatherapp.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.graphics.BitmapFactory
import android.util.Log
import com.kronos.core.extensions.formatDate
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationGroup
import com.kronos.core.notification.NotificationType
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.setLanguageForApp
import com.kronos.core.util.updateWidget
import com.kronos.domian.model.Response
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.R
import com.kronos.weatherapp.TAG
import com.kronos.weatherapp.widget.WeatherWidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.util.*
import javax.inject.Inject

const val notificationJobId = 1

@AndroidEntryPoint
class WeatherNotificationJob : JobService() {

    private var jobCancelled = false

    @Inject
    lateinit var weatherRemoteRepository: WeatherRemoteRepository

    @Inject
    lateinit var userCustomLocationLocalRepository: UserCustomLocationLocalRepository

    @Inject
    lateinit var notification: INotifications

    @Inject
    lateinit var logger: ILogger

    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStartJob")
        Log.d(TAG, "Current job started: ${params.jobId}")
        Log.d(TAG, "Current Job Params: ${params.jobId}")
        logger.write(this::class.java.name, LoggerType.INFO, "Current job started: ${params.jobId} on ${Date().formatDate("dd-MM-yyyy")}")
        logger.write(this::class.java.name, LoggerType.INFO, "Current Job Params: ${params.jobId}")
        doWork(params)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStopJob")
        Log.d(TAG, "Current job stopped: ${params.jobId}")
        logger.write(this::class.java.name, LoggerType.INFO, "Current job stopped: ${params.jobId} on ${Date().formatDate("dd-MM-yyyy")}")
        return true
    }

    private fun doWork(params: JobParameters) {
        Log.d(TAG, "doWork")
        Log.d(TAG, "Current Do Work Params: ${params.jobId}")
        logger.write(
            this::class.java.name,
            LoggerType.INFO,
            "Current Do Work Params: ${params.jobId} on ${Date().formatDate("dd-MM-yyyy")}"
        )
        if (params.jobId == notificationJobId) {
            refreshWeather(params)
        }
    }

    private fun refreshWeather(params: JobParameters) {
        runBlocking(Dispatchers.IO) {
            setLanguageForApp(baseContext,PreferencesUtil.getPreference(applicationContext,applicationContext.getString(R.string.default_lang_key),applicationContext.getString(R.string.default_language_value))!!)
            logger.write(
                this::class.java.name,
                LoggerType.INFO,
                "Current Do Work Params: Refreshing weather on ${Date().formatDate("dd-MM-yyyy")}"
            )
            var currentCity = userCustomLocationLocalRepository.getSelectedLocation()
            if (currentCity == null)
                currentCity = userCustomLocationLocalRepository.getCurrentLocation()

            val response: Response<Forecast>
            if (currentCity!=null){
                if (currentCity.isCurrent){
                    response = weatherRemoteRepository.getWeatherDataForecast(
                        currentCity.lat!!,
                        currentCity.lon!!,
                        PreferencesUtil.getPreference(applicationContext,applicationContext.getString(R.string.default_lang_key),applicationContext.getString(R.string.default_language_value))!!,
                        applicationContext.resources.getString(R.string.api_key),
                        PreferencesUtil.getPreference(applicationContext,application.getString(R.string.default_days_key),applicationContext.resources.getString(R.string.default_days_values))!!.toInt()
                    )
                }else{
                    response = weatherRemoteRepository.getWeatherDataForecast(
                        currentCity.cityName,
                        PreferencesUtil.getPreference(applicationContext,applicationContext.getString(R.string.default_lang_key),applicationContext.getString(R.string.default_language_value))!!,
                        applicationContext.resources.getString(R.string.api_key),
                        PreferencesUtil.getPreference(applicationContext,application.getString(R.string.default_days_key),applicationContext.resources.getString(R.string.default_days_values))!!.toInt()
                    )
                }
            }else{
                response = weatherRemoteRepository.getWeatherDataForecast(
                    PreferencesUtil.getPreference(applicationContext,application.getString(R.string.default_city_key),applicationContext.getString(R.string.default_city_value))!!,
                    PreferencesUtil.getPreference(applicationContext,applicationContext.getString(R.string.default_lang_key),applicationContext.getString(R.string.default_language_value))!!,
                    applicationContext.resources.getString(R.string.api_key),
                    PreferencesUtil.getPreference(applicationContext,application.getString(R.string.default_days_key),applicationContext.resources.getString(R.string.default_days_values))!!.toInt()
                )
            }
            if (response.data != null) {
                notification.createNotification(
                    applicationContext.getString(R.string.notification_title).format(response.data!!.current.tempC,response.data!!.location.region),
                    applicationContext.getString(R.string.notification_short_details)
                        .format(
                            response.data!!.current.condition.description,
                            response.data!!.current.feelslikeC
                        ),
                    applicationContext.getString(R.string.notification_long_details)
                        .format(
                            response.data!!.current.condition.description,
                            response.data!!.current.feelslikeC,
                            response.data!!.forecast.forecastDay[0].day.mintempC.toString(),
                            response.data!!.forecast.forecastDay[0].day.maxtempC.toString(),
                            response.data!!.forecast.forecastDay[0].day.dailyChanceOfRain.toString()
                        ),
                    NotificationGroup.GENERAL.name,
                    NotificationType.WEATHER_STATUS,
                    R.drawable.ic_weather_app_icon,
                    applicationContext,
                    BitmapFactory.decodeStream(URL("https:${response.data!!.current.condition.icon}").openConnection().getInputStream())
                )
            }
            updateWidget(applicationContext, WeatherWidgetProvider::class.java)
        }
    }

}






