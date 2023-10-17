package com.kronos.weatherapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.kronos.core.extensions.isToday
import com.kronos.core.extensions.of
import com.kronos.domian.model.DailyForecast
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.R
import com.kronos.webclient.UrlProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class WeatherWidgetProvider @Inject constructor() : AppWidgetProvider() {

    @Inject
    lateinit var customUserCustomLocationLocalRepository: UserCustomLocationLocalRepository

    @Inject
    lateinit var weatherRemoteRepository: WeatherRemoteRepository

    @Inject
    lateinit var logger: ILogger

    @Inject
    lateinit var urlProvider: UrlProvider


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)
        getWeather(remoteViews,context)
        val intent = Intent(context, WeatherWidgetProvider::class.java)
        intent.action = "REFRESH"
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.widget_layout_header, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun getWeather(remoteViews: RemoteViews, context: Context) {
        runBlocking(Dispatchers.IO) {
            remoteViews.setViewVisibility(R.id.widget_progress_bar,View.VISIBLE)
            remoteViews.setViewVisibility(R.id.widget_image_view_refresh,View.GONE)

            var currentCity = customUserCustomLocationLocalRepository.getSelectedLocation()
            if (currentCity == null)
                currentCity = customUserCustomLocationLocalRepository.getCurrentLocation()

            var response = weatherRemoteRepository.getWeatherDataForecast(
                currentCity!!.cityName,
                context.resources.getString(R.string.default_language),
                context.resources.getString(R.string.api_key),
                context.resources.getInteger(R.integer.default_days)
            )

            if (response.data != null) {
                var list = arrayListOf<DailyForecast>()
                list.addAll(response.data!!.forecast.forecastDay.filter {
                    !Date().of(it.date)!!.isToday()
                }.subList(0, 3))

                remoteViews.setTextViewText(R.id.widget_text_view_day_1, "${getDay(list[0])}")
                remoteViews.setTextViewText(R.id.widget_text_view_day_2, "${getDay(list[1])}")
                remoteViews.setTextViewText(R.id.widget_text_view_day_3, "${getDay(list[2])}")

                var urlCondition1 = URL(urlProvider.getImageUrl(list[0].day.condition.icon))
                var urlCondition2 = URL(urlProvider.getImageUrl(list[1].day.condition.icon))
                var urlCondition3 = URL(urlProvider.getImageUrl(list[2].day.condition.icon))

                val bmp1 =
                    BitmapFactory.decodeStream(urlCondition1.openConnection().getInputStream())
                val bmp2 =
                    BitmapFactory.decodeStream(urlCondition2.openConnection().getInputStream())
                val bmp3 =
                    BitmapFactory.decodeStream(urlCondition3.openConnection().getInputStream())
                remoteViews.setImageViewBitmap(R.id.widget_image_view_day_1, bmp1)
                remoteViews.setImageViewBitmap(R.id.widget_image_view_day_2, bmp2)
                remoteViews.setImageViewBitmap(R.id.widget_image_view_day_3, bmp3)

                var urlCondition =
                    URL(urlProvider.getImageUrl(response.data!!.current.condition.icon))
                val bmp = BitmapFactory.decodeStream(urlCondition.openConnection().getInputStream())
                remoteViews.setImageViewBitmap(R.id.widget_image_view_current, bmp)

                remoteViews.setTextViewText(
                    R.id.widget_text_view_location,
                    "${response.data!!.location.name}/${response.data!!.location.country}"
                )
                remoteViews.setTextViewText(
                    R.id.widget_text_view_temp,
                    String.format(
                        context.getString(
                            R.string.temp_celsius,
                            response.data!!.current.tempC.toString()
                        )
                    )
                )
            }
            remoteViews.setViewVisibility(R.id.widget_progress_bar,View.GONE)
            remoteViews.setViewVisibility(R.id.widget_image_view_refresh,View.VISIBLE)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)
        val widget = ComponentName(context, WeatherWidgetProvider::class.java)
        getWeather(remoteViews,context)
        appWidgetManager.updateAppWidget(widget, remoteViews)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val views = RemoteViews(context.packageName, R.layout.weather_widget)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getDay(current: DailyForecast?): String {
        var day = ""
        if (current != null) {
            var date = Date().of(current.date)

            val calendar = Calendar.getInstance()
            calendar.time = date

            val today = Calendar.getInstance()
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {
                day = "Today"
            } else {
                today.add(Calendar.DAY_OF_YEAR, 1)
                day = if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                ) {
                    "Tomorrow"
                } else {
                    //val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    val dayOfWeekString = SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
                    dayOfWeekString
                }
            }
        }
        return day
    }

}