package com.kronos.weatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.gms.location.LocationServices
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.core.extensions.isToday
import com.kronos.core.extensions.of
import com.kronos.core.util.show
import com.kronos.domian.model.DailyForecast
import com.kronos.domian.model.Hour
import com.kronos.domian.model.forecast.Forecast
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.FragmentWeatherBinding
import com.kronos.weatherapp.ui.weather.model.Indicator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*

@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private val binding by fragmentBinding<FragmentWeatherBinding>(R.layout.fragment_weather)

    private val viewModel by viewModels<WeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@WeatherFragment.viewModel
        lifecycleOwner = this@WeatherFragment.viewLifecycleOwner
        root
    }

    override fun onResume() {
        super.onResume()
        initViewModel()
        initViews()
        setListeners()
        observeViewModel()
    }

    private fun setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.initLocations()
        }
    }

    private fun observeViewModel() {
        viewModel.weather.observe(this.viewLifecycleOwner, ::handleWeather)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.error.observe(this.viewLifecycleOwner, ::handleError)
    }


    private fun handleError(hashtable: Hashtable<String, String>) {
        if (hashtable["error"] != null) {
            if (hashtable["error"]!!.isNotEmpty()) {
                show(
                    binding.recyclerViewWeatherByDay,
                    hashtable["error"].orEmpty(),
                    R.color.white,
                    R.color.primary_dark
                )
            } else {
                show(
                    binding.recyclerViewWeatherByDay,
                    hashtable["error"].orEmpty(),
                    R.color.white,
                    R.color.primary_dark
                )
            }
        }
    }

    private fun handleLoading(b: Boolean) {
        if (b != binding.swipeRefreshLayout.isRefreshing)
            binding.swipeRefreshLayout.isRefreshing = b
    }

    private fun handleWeather(weather: Forecast?) {
        if (weather != null) {
            var currentDayForecast: DailyForecast? = null
            val hours = mutableListOf<Hour>()
            if (weather.forecast.forecastDay.isNotEmpty()) {
                for (item in weather.forecast.forecastDay) {
                    val date = Date().of(item.date)
                    if (date != null) {
                        if (date.isToday()) {
                            currentDayForecast = item
                            break;
                        }
                    }
                }
                if (currentDayForecast == null) {
                    currentDayForecast = weather.forecast.forecastDay[0]
                }
                for (item in currentDayForecast.hours) {
                    val date = Date().of(item.time, true)
                    if (date!!.after(Date())) {
                        hours.add(item)
                    }
                }
            }

            val indicator = listOf(
                Indicator(
                    getString(R.string.wind),
                    requireContext().getString(
                        R.string.speed_km,
                        weather.current?.windSpeedKph.toString()
                    ),
                    requireContext().resources.getDrawable(R.drawable.ic_blowing_climate_forecast)
                ),
                Indicator(
                    getString(R.string.humidity),
                    String.format("%.1f%%", weather?.current?.windSpeedKph),
                    requireContext().resources.getDrawable(R.drawable.ic_humidity)
                ),
                Indicator(
                    getString(R.string.uv_index),
                    weather?.current?.uv.toString(),
                    requireContext().resources.getDrawable(R.drawable.ic_day_forecast_hot)
                ),
                Indicator(
                    getString(R.string.rain),
                    String.format("%.1fmm", weather?.current?.precipitationMm),
                    requireContext().resources.getDrawable(R.drawable.ic_climate_cloud_forecast)
                ),
            )
            viewModel.indicatorAdapter.get()?.submitList(indicator)
            viewModel.indicatorAdapter.get()?.notifyDataSetChanged()

            var list = arrayListOf<DailyForecast>()
            list.addAll(weather.forecast.forecastDay.filter {
                !Date().of(it.date)!!.isToday()
            })

            viewModel.hourWeatherAdapter.get()?.submitList(hours)
            viewModel.hourWeatherAdapter.get()?.notifyDataSetChanged()

            viewModel.dailyWeatherAdapter.get()?.submitList(list)
            viewModel.dailyWeatherAdapter.get()?.notifyDataSetChanged()
            Glide.with(requireContext())
                .load(viewModel.urlProvider.getImageUrl(weather.current.condition.icon))
                .into(binding.imageCurrentWeather)

            viewModel.loading.postValue(false)
        }
    }


    private fun initViews() {
        binding.recyclerViewWeatherHourly.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewWeatherHourly.setHasFixedSize(false)
        if (viewModel.hourWeatherAdapter.get() == null)
            viewModel.hourWeatherAdapter = WeakReference(WeatherHourAdapter())
        viewModel.hourWeatherAdapter.get()?.setUrlProvider(viewModel.urlProvider)
        binding.recyclerViewWeatherHourly.adapter = viewModel.hourWeatherAdapter.get()
        viewModel.hourWeatherAdapter.get()?.setAdapterItemClick(object :
            AdapterItemClickListener<Hour> {
            override fun onItemClick(t: Hour, pos: Int) {
                Toast.makeText(requireContext(),t.condition.description,Toast.LENGTH_SHORT).show()
            }

        })

        binding.recyclerViewWeatherByDay.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewWeatherByDay.setHasFixedSize(false)
        if (viewModel.dailyWeatherAdapter.get() == null)
            viewModel.dailyWeatherAdapter = WeakReference(WeatherDayAdapter())
        viewModel.dailyWeatherAdapter.get()?.setUrlProvider(viewModel.urlProvider)
        binding.recyclerViewWeatherByDay.adapter = viewModel.dailyWeatherAdapter.get()

        binding.recyclerViewIndicator.layoutManager = GridLayoutManager(context, 4)
        binding.recyclerViewIndicator.setHasFixedSize(false)
        if (viewModel.indicatorAdapter.get() == null)
            viewModel.indicatorAdapter = WeakReference(IndicatorAdapter())
        viewModel.indicatorAdapter.get()?.setUrlProvider(viewModel.urlProvider)
        binding.recyclerViewIndicator.adapter = viewModel.indicatorAdapter.get()

    }

    private fun initViewModel() {
        viewModel.postDate(Date())
        viewModel.loading.value = (true)
        if (viewModel.locationManager.get() == null)
            viewModel.postLocationManager(
                LocationServices.getFusedLocationProviderClient(
                    requireContext()
                )
            )
        //if(viewModel.weather.value==null)
        viewModel.initLocations()
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }

    override fun onPause() {
        viewModel.destroy()
        super.onPause()
    }


}