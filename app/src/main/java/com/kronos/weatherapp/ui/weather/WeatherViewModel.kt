package com.kronos.weatherapp.ui.weather

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.kronos.core.extensions.asLiveData
import com.kronos.core.extensions.formatDate
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationGroup
import com.kronos.core.notification.NotificationType
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domian.model.Response
import com.kronos.domian.model.UserCustomLocation
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.R
import com.kronos.webclient.UrlProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val weatherRemoteRepository: WeatherRemoteRepository,
    private val userCustomLocationLocalRepository: UserCustomLocationLocalRepository,
    private val notification: INotifications,
    val urlProvider: UrlProvider,
    val logger: ILogger,
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

    private fun getWeather(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                val response = weatherRemoteRepository.getWeatherDataForecast(
                    lat,
                    long,
                    PreferencesUtil.getPreference(
                        context,
                        context.getString(R.string.default_lang_key),
                        context.getString(R.string.default_language_value)
                    )!!,
                    context.resources.getString(R.string.api_key),
                    PreferencesUtil.getPreference(
                        context,
                        context.getString(R.string.default_days_key),
                        context.getString(R.string.default_days_values)
                    )!!.toInt()
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
                e.printStackTrace()
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    private fun getWeather(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                val response = weatherRemoteRepository.getWeatherDataForecast(
                    city,
                    PreferencesUtil.getPreference(
                        context,
                        context.getString(R.string.default_lang_key),
                        context.getString(R.string.default_language_value)
                    )!!,
                    context.resources.getString(R.string.api_key),
                    PreferencesUtil.getPreference(
                        context,
                        context.getString(R.string.default_days_key),
                        context.getString(R.string.default_days_values)
                    )!!.toInt()
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
                e.printStackTrace()
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
        var city = context.getString(R.string.default_city_value)
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses?.isNotEmpty() == true) city = addresses[0].locality
        } catch (e: IOException) {
            e.printStackTrace()
            log(e.message.toString(), LoggerType.ERROR)
        }
        return city
    }

    fun initLocations() {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var response = userCustomLocationLocalRepository.getSelectedLocation()
                if (response == null) {
                    response = userCustomLocationLocalRepository.getCurrentLocation()
                }
                if (response != null) {
                    postUserLocation(response)
                    if (response.isCurrent && response.isSelected)
                        getGpsLocation(response)
                    else {
                        getWeather(response.cityName)
                    }
                } else {
                    getGpsLocation(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Weather error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    private fun getGpsLocation(userLocation: UserCustomLocation?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if ((context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                    )
                ) {
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
                            //var city = getCityName(context, p0.lastLocation!!)
                            /*getWeather(city)
                            saveCurrentLocation(city,p0.lastLocation!!)*/
                        }
                    }

                    locationManager.get()?.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    locationManager.get()?.lastLocation?.addOnSuccessListener {
                        if (it != null) {
                            val city = getCityName(context, it)
                            getWeather(it.latitude, it.longitude)
                            saveCurrentLocation(city, it)
                        }
                    }
                } else{
                    if (userLocation!=null){
                        if (userLocation.lat!=null && userLocation.lon!=null)
                            getWeather(userLocation.lat!!, userLocation.lon!!)
                        else{
                            getWeather(userLocation.cityName)
                        }
                    }else{
                        getWeather(
                            PreferencesUtil.getPreference(
                                context,
                                context.getString(R.string.default_city_key),
                                context.getString(R.string.default_city_value)
                            )!!
                        )
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
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

    fun sendNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            logger.write(
                this::class.java.name,
                LoggerType.INFO,
                "Updating notification at exit on ${Date().formatDate("dd-MM-yyyy")}"
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
                        PreferencesUtil.getPreference(context,context.getString(R.string.default_lang_key),context.getString(R.string.default_language_value))!!,
                        context.resources.getString(R.string.api_key),
                        PreferencesUtil.getPreference(context,context.getString(R.string.default_days_key),context.resources.getString(R.string.default_days_values))!!.toInt()
                    )
                }else{
                    response = weatherRemoteRepository.getWeatherDataForecast(
                        currentCity.cityName,
                        PreferencesUtil.getPreference(context,context.getString(R.string.default_lang_key),context.getString(R.string.default_language_value))!!,
                        context.resources.getString(R.string.api_key),
                        PreferencesUtil.getPreference(context,context.getString(R.string.default_days_key),context.resources.getString(R.string.default_days_values))!!.toInt()
                    )
                }
            }else{
                response = weatherRemoteRepository.getWeatherDataForecast(
                    PreferencesUtil.getPreference(context,context.getString(R.string.default_city_key),context.getString(R.string.default_city_value))!!,
                    PreferencesUtil.getPreference(context,context.getString(R.string.default_lang_key),context.getString(R.string.default_language_value))!!,
                    context.resources.getString(R.string.api_key),
                    PreferencesUtil.getPreference(context,context.getString(R.string.default_days_key),context.resources.getString(R.string.default_days_values))!!.toInt()
                )
            }
            if (response.data != null) {
                notification.createNotification(
                    context.getString(R.string.notification_title).format(response.data!!.current.tempC,response.data!!.location.region),
                    context.getString(R.string.notification_short_details)
                        .format(
                            response.data!!.current.condition.description,
                            response.data!!.current.feelslikeC
                        ),
                    context.getString(R.string.notification_long_details)
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
                    context,
                    BitmapFactory.decodeStream(URL("https:${response.data!!.current.condition.icon}").openConnection().getInputStream())
                )
            }
        }
    }

}