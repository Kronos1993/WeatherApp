package com.kronos.weatherapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import com.kronos.logger.LoggerType
import com.kronos.logger.exception.ExceptionHandler
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.job.WeatherNotificationJob
import com.kronos.weatherapp.job.notificationJobId
import com.kronos.weatherapp.notification.WeatherAppNotifications
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

const val TAG = "WeatherApp"
const val NOTIFICATION_CHANNEL = "WEATHER_APP_NOTIFICATION_CHANNEL"
@HiltAndroidApp
class WeatherApp:Application(){

    @Inject
    lateinit var logger: ILogger
    @Inject
    lateinit var exceptionHandler: ExceptionHandler

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChanel()
            scheduleJob(applicationContext, 7200000L)
            exceptionHandler.init(this)
            logger.configure()
        }catch (e:Exception){
            logger.write(this::class.java.name, LoggerType.ERROR, e.message.toString())
        }
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun scheduleJob(context: Context, periodic: Long) {
        try {
            val componentName = ComponentName(context, WeatherNotificationJob::class.java)
            var jobInfo: JobInfo? = null

            jobInfo = JobInfo.Builder(notificationJobId, componentName)
                .setPersisted(true)
                .setPeriodic(periodic)
                .build()

            val scheduler =
                context.getSystemService(JobService.JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = scheduler.schedule(jobInfo!!)
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job service schedule success $notificationJobId")
            } else {
                Log.d(TAG, "Job service schedule failed $notificationJobId")
            }
        }catch (e:Exception){
            logger.write(this::class.java.name, LoggerType.ERROR, e.message.toString())

        }
    }
}