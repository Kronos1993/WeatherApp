package com.kronos.weatherapp.ui.weather

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domian.model.Response
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.R
import com.kronos.webclient.UrlProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var weatherRemoteRepository: WeatherRemoteRepository,
    var urlProvider: UrlProvider,
    var logger: ILogger,
) : ParentViewModel() {

    private val _weather = MutableLiveData<Forecast>()
    val weather = _weather.asLiveData()

    var hourWeatherAdapter: WeakReference<WeatherHourAdapter?> = WeakReference(WeatherHourAdapter())

    var dailyWeatherAdapter: WeakReference<WeatherDayAdapter?> = WeakReference(WeatherDayAdapter())

    var indicatorAdapter: WeakReference<IndicatorAdapter?> = WeakReference(IndicatorAdapter())

    private fun postWeather(weather: Response<Forecast>) {
        _weather.postValue(weather.data!!)
        loading.postValue(false)
    }

    fun getWeather(city: String) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var call = async {
                    val response = weatherRemoteRepository.getWeatherDataForecast(
                        city,
                        context.resources.getString(R.string.default_language),
                        context.resources.getString(R.string.api_key),
                        context.resources.getInteger(R.integer.default_days)
                    )
                    log("Weather from city: ${response.data?.location?.name} acquired")
                    if(response.ex==null)
                        postWeather(response)
                    else{
                        var err = Hashtable<String,String>()
                        err["error"] = response.ex!!.message
                        error.postValue(err)
                        loading.postValue(false)
                        log("Weather error : ${response.ex!!.message}")
                    }
                }
                call.await()
            } catch (e: Exception) {
                var err = Hashtable<String,String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}")
            }
        }
    }


    private fun log(item: String) {
        viewModelScope.launch {
            logger.write(this::class.java.name, LoggerType.INFO, item)
        }
    }
}