package com.kronos.weatherapp.ui.weather

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.kronos.core.extensions.asLiveData
import com.kronos.core.util.updateWidget
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domian.model.Response
import com.kronos.domian.model.UserCustomLocation
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.R
import com.kronos.weatherapp.widget.WeatherWidgetProvider
import com.kronos.webclient.UrlProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var weatherRemoteRepository: WeatherRemoteRepository,
    var userCustomLocationLocalRepository: UserCustomLocationLocalRepository,
    var urlProvider: UrlProvider,
    var logger: ILogger,
) : ParentViewModel() {

    private val _weather = MutableLiveData<Forecast?>()
    val weather = _weather.asLiveData()

    var hourWeatherAdapter: WeakReference<WeatherHourAdapter?> = WeakReference(WeatherHourAdapter())

    var dailyWeatherAdapter: WeakReference<WeatherDayAdapter?> = WeakReference(WeatherDayAdapter())

    var indicatorAdapter: WeakReference<IndicatorAdapter?> = WeakReference(IndicatorAdapter())

    var locationManager: WeakReference<FusedLocationProviderClient?> = WeakReference(null)
    private lateinit var locationCallback: LocationCallback

    private val _selectedUserLocation = MutableLiveData<UserCustomLocation?>()
    val selectedUserLocation = _selectedUserLocation.asLiveData()

    private fun postWeather(weather: Response<Forecast>) {
        _weather.postValue(weather.data)
    }

    private fun postUserLocation(userLocation: UserCustomLocation) {
        _selectedUserLocation.postValue(userLocation)
    }

    fun postLocationManager(locationProvider: FusedLocationProviderClient) {
        locationManager = WeakReference(locationProvider)
    }

    fun postDate(date: Date) {
        this.date.postValue(date)
    }

    private fun getWeather(lat: Double,long:Double) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                val response = weatherRemoteRepository.getWeatherDataForecast(
                    lat,
                    long,
                    context.resources.getString(R.string.default_language),
                    context.resources.getString(R.string.api_key),
                    context.resources.getInteger(R.integer.default_days)
                )
                log(
                    "Weather from city: ${response.data?.location?.name} acquired",
                    LoggerType.INFO
                )
                if (response.ex == null) {
                    postWeather(response)
                } else {
                    val err = Hashtable<String, String>()
                    err["error"] = response.ex!!.message
                    error.postValue(err)
                    log("Weather error : ${response.ex!!.message}", LoggerType.ERROR)
                }
            } catch (e: Exception) {
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    private fun getWeather(city:String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                val response = weatherRemoteRepository.getWeatherDataForecast(
                    city,
                    context.resources.getString(R.string.default_language),
                    context.resources.getString(R.string.api_key),
                    context.resources.getInteger(R.integer.default_days)
                )
                log(
                    "Weather from city: ${response.data?.location?.name} acquired",
                    LoggerType.INFO
                )
                if (response.ex == null) {
                    postWeather(response)
                } else {
                    val err = Hashtable<String, String>()
                    err["error"] = response.ex!!.message
                    error.postValue(err)
                    log("Weather error : ${response.ex!!.message}", LoggerType.ERROR)
                    loading.postValue(false)
                }
            } catch (e: Exception) {
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}", LoggerType.ERROR)
            }
        }
    }


    private fun log(item: String, loggerType: LoggerType) {
        viewModelScope.launch(Dispatchers.IO) {
            logger.write(this::class.java.name, loggerType, item)
        }
    }

    private fun getCityName(context: Context, location: Location): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                return addresses[0].locality
            }
        } catch (e: IOException) {
            log(e.message.toString(), LoggerType.ERROR)
        }
        return "Ciudad Desconocida"
    }

    fun initLocations() {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var response = userCustomLocationLocalRepository.getSelectedLocation()
                if (response != null) {
                    postUserLocation(response!!)
                    if (response!!.isCurrent && response!!.isSelected)
                        getGpsLocation()
                    else{
                        getWeather(response!!.cityName)
                    }
                } else {
                    getGpsLocation()
                }
            } catch (e: Exception) {
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    private fun getGpsLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                locationManager.get()?.lastLocation?.addOnSuccessListener {
                    if (it != null) {
                        var city = getCityName(context, it)
                        getWeather(it.latitude,it.longitude)
                        saveCurrentLocation(city, it)
                    }
                }

                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    1800000
                ).apply {
                    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    setWaitForAccurateLocation(true)
                }.build()

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                        /*var city = getCityName(context, p0.lastLocation)
                        getWeather(city)
                        saveCurrentLocation(city,location)*/
                    }
                }

                locationManager.get()?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    private fun saveCurrentLocation(cityName: String, location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            val userLocation = UserCustomLocation(
                cityName = cityName,
                isCurrent = true,
                isSelected = true,
                lat = location.latitude,
                lon = location.longitude
            )
            if (selectedUserLocation.value?.id != null) {
                userLocation.id = selectedUserLocation.value?.id!!
                userLocation.isCurrent = selectedUserLocation.value?.isCurrent == true
            }

            userCustomLocationLocalRepository.saveLocation(userLocation)
        }
    }

    fun destroy() {
        postWeather(Response())
        hourWeatherAdapter = WeakReference(WeatherHourAdapter())
        dailyWeatherAdapter = WeakReference(WeatherDayAdapter())
        indicatorAdapter = WeakReference(IndicatorAdapter())
        locationManager = WeakReference(null)
    }

}