package com.kronos.weatherapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.RemoteViews
import com.kronos.core.extensions.capitalizeFirstLetter
import com.kronos.core.extensions.formatDate
import com.kronos.core.extensions.isToday
import com.kronos.core.extensions.of
import com.kronos.core.util.PreferencesUtil
import com.kronos.domian.model.DailyForecast
import com.kronos.domian.model.Response
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.MainActivity
import com.kronos.weatherapp.R
import com.kronos.webclient.UrlProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
        logger.write(this::class.java.name, LoggerType.INFO, "Widget on update")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        logger.write(this::class.java.name, LoggerType.INFO, "Widget on update app widget")

        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        val layoutId = when {
            minWidth <= 50 && minHeight <= 50 -> R.layout.weather_widget_small
            minWidth >= 120 && minHeight >= 120 -> R.layout.weather_widget_large
            else -> R.layout.weather_widget
        }

        val remoteViews = RemoteViews(context.packageName, layoutId)
        println("loading weather from on update widget")

        // Set up the default click behavior
        val configIntent = Intent(context, MainActivity::class.java)
        val configPendingIntent =
            PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, configPendingIntent)

        when (layoutId) {
            R.layout.weather_widget -> getWeather(remoteViews, context)
            R.layout.weather_widget_small -> getWeatherSmall(remoteViews, context)
            R.layout.weather_widget_large -> getWeather(remoteViews, context)
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun getWeather(remoteViews: RemoteViews, context: Context) {
        logger.write(this::class.java.name, LoggerType.INFO, "Get weather widget")

        runBlocking(Dispatchers.IO) {
            try {
                var currentCity = customUserCustomLocationLocalRepository.getSelectedLocation()
                if (currentCity == null)
                    currentCity = customUserCustomLocationLocalRepository.getCurrentLocation()

                val response: Response<Forecast>
                if (currentCity != null) {
                    if (currentCity.isCurrent) {
                        response = weatherRemoteRepository.getWeatherDataForecast(
                            currentCity.lat!!,
                            currentCity.lon!!,
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_lang_key),
                                context.getString(R.string.default_language_value)
                            )!!,
                            context.resources.getString(R.string.api_key),
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_days_key),
                                context.resources.getString(R.string.default_days_values)
                            )!!.toInt()
                        )
                    } else {
                        response = weatherRemoteRepository.getWeatherDataForecast(
                            currentCity.cityName,
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_lang_key),
                                context.getString(R.string.default_language_value)
                            )!!,
                            context.resources.getString(R.string.api_key),
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_days_key),
                                context.resources.getString(R.string.default_days_values)
                            )!!.toInt()
                        )
                    }
                } else {
                    response = weatherRemoteRepository.getWeatherDataForecast(
                        PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_city_key),
                            context.getString(R.string.default_city_value)
                        )!!,
                        PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_lang_key),
                            context.getString(R.string.default_language_value)
                        )!!,
                        context.resources.getString(R.string.api_key),
                        PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_days_key),
                            context.resources.getString(R.string.default_days_values)
                        )!!.toInt()
                    )
                }

                if (response.data != null) {
                    val list = arrayListOf<DailyForecast>()
                    list.addAll(response.data!!.forecast.forecastDay.filter {
                        !Date().of(it.date)!!.isToday()
                    })

                    if (list.size >= 2) {
                        for (i in 1..list.size) {
                            if (i == 1) {
                                remoteViews.setTextViewText(
                                    R.id.widget_text_view_day_1,
                                    getDay(context, list[0])
                                )
                                val urlCondition1 = URL(
                                    urlProvider.getImageUrl(
                                        list[0].day.condition.icon,
                                        PreferencesUtil.getPreference(
                                            context,
                                            context.getString(R.string.default_image_quality_key),
                                            context.getString(R.string.default_image_quality_value)
                                        )!!
                                    )
                                )
                                val bmp1 =
                                    BitmapFactory.decodeStream(
                                        urlCondition1.openConnection().getInputStream()
                                    )
                                remoteViews.setImageViewBitmap(R.id.widget_image_view_day_1, bmp1)
                            } else if (i == 2) {
                                remoteViews.setTextViewText(
                                    R.id.widget_text_view_day_2,
                                    getDay(context, list[i - 1])
                                )
                                val urlCondition2 = URL(
                                    urlProvider.getImageUrl(
                                        list[i - 1].day.condition.icon,
                                        PreferencesUtil.getPreference(
                                            context,
                                            context.getString(R.string.default_image_quality_key),
                                            context.getString(R.string.default_image_quality_value)
                                        )!!
                                    )
                                )
                                val bmp2 =
                                    BitmapFactory.decodeStream(
                                        urlCondition2.openConnection().getInputStream()
                                    )
                                remoteViews.setImageViewBitmap(R.id.widget_image_view_day_2, bmp2)
                            } else {
                                break
                            }
                        }
                    }

                    val urlCondition =
                        URL(
                            urlProvider.getImageUrl(
                                response.data!!.current.condition.icon,
                                PreferencesUtil.getPreference(
                                    context,
                                    context.getString(R.string.default_image_quality_key),
                                    context.getString(R.string.default_image_quality_value)
                                )!!
                            )
                        )
                    val bmp =
                        BitmapFactory.decodeStream(urlCondition.openConnection().getInputStream())
                    remoteViews.setImageViewBitmap(R.id.widget_image_view_current, bmp)

                    remoteViews.setTextViewText(
                        R.id.widget_text_view_time,
                        Date().of(response.data!!.location.localtime, true)!!
                            .formatDate("hh:mm aa dd-MM")
                    )

                    remoteViews.setTextViewText(
                        R.id.widget_text_view_location,
                        response.data!!.location.name
                    )

                    remoteViews.setTextViewText(
                        R.id.widget_text_view_temp,
                        String.format(
                            context.getString(
                                R.string.temp_celsius_widget
                            ),
                            response.data!!.current.tempC.toString()
                        )
                    )
                }
            } catch (e: Exception) {
                logger.write(
                    this::class.java.name,
                    LoggerType.ERROR,
                    "Widget on update error ${e.message.toString()}"
                )
            }
        }
    }

    private fun getWeatherSmall(remoteViews: RemoteViews, context: Context) {
        logger.write(this::class.java.name, LoggerType.INFO, "Get weather widget")

        runBlocking(Dispatchers.IO) {
            try {
                var currentCity = customUserCustomLocationLocalRepository.getSelectedLocation()
                if (currentCity == null)
                    currentCity = customUserCustomLocationLocalRepository.getCurrentLocation()

                val response: Response<Forecast>
                if (currentCity != null) {
                    if (currentCity.isCurrent) {
                        response = weatherRemoteRepository.getWeatherDataForecast(
                            currentCity.lat!!,
                            currentCity.lon!!,
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_lang_key),
                                context.getString(R.string.default_language_value)
                            )!!,
                            context.resources.getString(R.string.api_key),
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_days_key),
                                context.resources.getString(R.string.default_days_values)
                            )!!.toInt()
                        )
                    } else {
                        response = weatherRemoteRepository.getWeatherDataForecast(
                            currentCity.cityName,
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_lang_key),
                                context.getString(R.string.default_language_value)
                            )!!,
                            context.resources.getString(R.string.api_key),
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_days_key),
                                context.resources.getString(R.string.default_days_values)
                            )!!.toInt()
                        )
                    }
                } else {
                    response = weatherRemoteRepository.getWeatherDataForecast(
                        PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_city_key),
                            context.getString(R.string.default_city_value)
                        )!!,
                        PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_lang_key),
                            context.getString(R.string.default_language_value)
                        )!!,
                        context.resources.getString(R.string.api_key),
                        PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_days_key),
                            context.resources.getString(R.string.default_days_values)
                        )!!.toInt()
                    )
                }

                if (response.data != null) {
                    val urlCondition =
                        URL(
                            urlProvider.getImageUrl(
                                response.data!!.current.condition.icon,
                                PreferencesUtil.getPreference(
                                    context,
                                    context.getString(R.string.default_image_quality_key),
                                    context.getString(R.string.default_image_quality_value)
                                )!!
                            )
                        )
                    val bmp =
                        BitmapFactory.decodeStream(urlCondition.openConnection().getInputStream())
                    remoteViews.setImageViewBitmap(R.id.widget_image_view_current, bmp)

                    remoteViews.setTextViewText(
                        R.id.widget_text_view_time,
                        Date().of(response.data!!.location.localtime, true)!!
                            .formatDate("hh:mm aa dd-MM")
                    )

                    remoteViews.setTextViewText(
                        R.id.widget_text_view_location,
                        response.data!!.location.name
                    )

                    remoteViews.setTextViewText(
                        R.id.widget_text_view_temp,
                        String.format(
                            context.getString(
                                R.string.temp_celsius_widget
                            ),
                            response.data!!.current.tempC.toString()
                        )
                    )
                }
            } catch (e: Exception) {
                logger.write(
                    this::class.java.name,
                    LoggerType.ERROR,
                    "Widget on update error ${e.message.toString()}"
                )
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        logger.write(this::class.java.name, LoggerType.INFO, "Widget on received")
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetId = intent.extras?.getInt("appWidgetId")
            if (appWidgetId!=null){
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }else{
                val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)
                val widget = ComponentName(context, WeatherWidgetProvider::class.java)
                appWidgetManager.updateAppWidget(widget, remoteViews)
            }

        } catch (e: Exception) {
            logger.write(
                this::class.java.name,
                LoggerType.ERROR,
                "Widget on received error ${e.message.toString()}"
            )
        }

    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        val layoutId = when {
            minWidth <= 120 && minHeight <= 120 -> R.layout.weather_widget_small
            minWidth >= 220 && minHeight >= 220 -> R.layout.weather_widget_large
            else -> R.layout.weather_widget
        }

        val remoteViews = RemoteViews(context.packageName, layoutId)

        // Set up the default click behavior
        val configIntent = Intent(context, MainActivity::class.java)
        val configPendingIntent =
            PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_IMMUTABLE)

        when (layoutId) {
            R.layout.weather_widget -> {
                getWeather(remoteViews, context)
                remoteViews.setOnClickPendingIntent(R.id.widget_layout, configPendingIntent)
            }
            R.layout.weather_widget_small -> {
                remoteViews.setOnClickPendingIntent(R.id.widget_layout_small, configPendingIntent)
                getWeatherSmall(remoteViews, context)
            }
            R.layout.weather_widget_large -> {
                remoteViews.setOnClickPendingIntent(R.id.widget_layout_large, configPendingIntent)
                getWeather(remoteViews, context)
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun getDay(context: Context, current: DailyForecast?): String {
        var day = ""
        if (current != null) {
            val date = Date().of(current.date)

            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

            val today = Calendar.getInstance()
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {
                day = context.getString(R.string.today)
            } else {
                today.add(Calendar.DAY_OF_YEAR, 1)
                day = if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                ) {
                    context.getString(R.string.tomorrow)
                } else {
                    //val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    val dayOfWeekString = if (PreferencesUtil.getPreference(
                            context,
                            context.getString(R.string.default_lang_key),
                            context.getString(R.string.default_language_value)
                        )!! == "en"
                    )
                        SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
                    else
                        SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
                    dayOfWeekString
                }
            }
        }
        return day.capitalizeFirstLetter()
    }

}