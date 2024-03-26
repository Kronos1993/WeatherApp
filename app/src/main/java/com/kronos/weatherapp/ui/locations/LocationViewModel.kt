package com.kronos.weatherapp.ui.locations

import android.content.Context
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domian.model.UserCustomLocation
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
import java.lang.ref.WeakReference
import java.util.Date
import java.util.Hashtable
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var userCustomLocationLocalRepository: UserCustomLocationLocalRepository,
    private val weatherRemoteRepository: WeatherRemoteRepository,
    val urlProvider: UrlProvider,
    var logger: ILogger,
): ParentViewModel() {

    private val _locations = MutableLiveData<List<UserCustomLocation>>()
    val locations = _locations.asLiveData()

    var userLocationAdapter: WeakReference<UserLocationAdapter?> = WeakReference(UserLocationAdapter())

    var cityName = ObservableField<String?>()
    var cityNameError = ObservableField<String?>()

    private fun postLocations(list: List<UserCustomLocation>) {
        _locations.postValue(list)
        loading.postValue(false)
    }

    fun postDate(date: Date) {
        this.date.value = date
    }

    fun getLocations() {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userCustomLocationLocalRepository.listAll()
                log("Custom location: ${response.size} ", LoggerType.INFO)
                for (location in response){
                    val weather = if (location.isCurrent){
                            weatherRemoteRepository.getWeatherDataForecast(
                                location.lat!!,
                                location.lon!!,
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
                        }else{
                        weatherRemoteRepository.getWeatherDataForecast(
                                location.cityName,
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
                        }
                        if (weather.ex==null){
                            location.icon = weather.data!!.current.condition.icon
                            location.tempC = weather.data!!.current.tempC
                        }
                    }
                postLocations(response)
            } catch (e: Exception) {
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Get locations error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    fun addLocation() {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val location = UserCustomLocation(cityName = cityName.get()!!, isSelected = true)
                userCustomLocationLocalRepository.saveLocation(location)
                log("Custom location: ${cityName.get()} added.", LoggerType.INFO)
                getLocations()
                cityName.set("")
            } catch (e: Exception) {
                val err = Hashtable<String, String>()
                err["error"] = e.message
                error.postValue(err)
                loading.postValue(false)
                log("Get locations error : ${e.message}", LoggerType.ERROR)
            }
        }
    }

    fun setLocationSelected(userLocation:UserCustomLocation) {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userLocation.isSelected = true
                userCustomLocationLocalRepository.saveLocation(userLocation)
                log("Custom location: ${cityName.get()} selected.", LoggerType.INFO)
                getLocations()
            } catch (e: Exception) {
                val err = Hashtable<String, String>()
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
        error.postValue(Hashtable<String, String>())
    }

    fun deleteLocation(itemAt: UserCustomLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!(itemAt.isCurrent || itemAt.isSelected)){
                try {
                    userCustomLocationLocalRepository.delete(itemAt)
                    log("Custom location deleted: ${itemAt.cityName} ", LoggerType.INFO)
                    getLocations()
                } catch (e: Exception) {
                    val err = Hashtable<String, String>()
                    err["error"] = e.message
                    error.postValue(err)
                    loading.postValue(false)
                    log("Delete location error : ${e.message}", LoggerType.ERROR)
                }
            }else{
                val err = Hashtable<String, String>()
                err["error"] = context.getString(R.string.cant_delete_current_location)
                error.postValue(err)
                log("Delete location error : current location", LoggerType.ERROR)
                getLocations()
            }
        }
    }

    fun validateField() : Boolean{
        var valid = true
        if (cityName.get().orEmpty().isEmpty()){
            valid = false
            cityNameError.set(context.getString(R.string.required_field))
        }else{
            cityNameError.set(null)
        }
        return valid
    }

    fun observeTextChange() {
        cityName.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(
                    sender: Observable?,
                    propertyId: Int
                ) {
                    if (cityName.get().orEmpty().isNotEmpty()){
                        cityNameError.set(null)
                    }
                }
            }
        )
    }
}