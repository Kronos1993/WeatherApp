package com.kronos.weatherapp.ui.locations

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domian.model.Response
import com.kronos.domian.model.UserCustomLocation
import com.kronos.domian.model.forecast.Forecast
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import com.kronos.domian.repository.WeatherRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.weatherapp.R
import com.kronos.weatherapp.ui.weather.IndicatorAdapter
import com.kronos.weatherapp.ui.weather.WeatherDayAdapter
import com.kronos.weatherapp.ui.weather.WeatherHourAdapter
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
class LocationViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var userCustomLocationLocalRepository: UserCustomLocationLocalRepository,
    var logger: ILogger,
): ParentViewModel() {

    private val _locations = MutableLiveData<List<UserCustomLocation>>()
    val locations = _locations.asLiveData()

    var userLocationAdapter: WeakReference<UserLocationAdapter?> = WeakReference(UserLocationAdapter())

    private fun postLocations(list: List<UserCustomLocation>) {
        _locations.postValue(list)
        loading.postValue(false)
    }

    fun getLocations() {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var call = async {
                    val response = userCustomLocationLocalRepository.listAll()
                    log("Custom location: ${response.size} ", LoggerType.INFO)
                    postLocations(response)
                }
                call.await()
            } catch (e: Exception) {
                var err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Get locations error : ${e.message}", LoggerType.ERROR)
            }
        }
    }


    private fun log(item: String,loggerType: LoggerType) {
        viewModelScope.launch {
            logger.write(this::class.java.name, loggerType , item)
        }
    }

    fun destroy() {
        postLocations(listOf())
        userLocationAdapter = WeakReference(UserLocationAdapter())
    }

    fun deleteLocation(itemAt: UserCustomLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!itemAt.isCurrent){
                try {
                    var call = async {
                        userCustomLocationLocalRepository.delete(itemAt)
                        log("Custom location deleted: ${itemAt.cityName} ", LoggerType.INFO)
                    }
                    call.await()
                    getLocations()
                } catch (e: Exception) {
                    var err = Hashtable<String, String>()
                    err["error"] = e.message
                    error.postValue(err)
                    loading.postValue(false)
                    log("Delete location error : ${e.message}", LoggerType.ERROR)
                }
            }else{
                getLocations()
            }
        }
    }

}