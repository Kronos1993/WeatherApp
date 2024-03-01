package com.kronos.weatherapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.core.util.PreferencesUtil
import com.kronos.domian.model.DailyForecast
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.ItemWeatherDailyBinding
import com.kronos.webclient.UrlProvider

class WeatherDayAdapter : ListAdapter<DailyForecast, WeatherDayAdapter.WeatherDayViewHolder>(GeneralDiffCallback<DailyForecast>()) {

    private var adapterItemClickListener:AdapterItemClickListener<DailyForecast>?=null
    private lateinit var urlProvider: UrlProvider

    fun setAdapterItemClick(adapterItemClickListener:AdapterItemClickListener<DailyForecast>?){
        this.adapterItemClickListener = adapterItemClickListener
    }

    fun setUrlProvider(urlProvider: UrlProvider){
        this.urlProvider = urlProvider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDayViewHolder {
        val binding = ItemWeatherDailyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WeatherDayViewHolder(binding,adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: WeatherDayViewHolder, position: Int) {
        val current = getItemAt(position)
        holder.bind(current)
        val context = holder.binding.imageViewCondition.context
        Glide.with(holder.binding.imageViewCondition)
            .load(urlProvider.getImageUrl(current.day.condition.icon,PreferencesUtil.getPreference(context,context.getString(R.string.default_image_quality_key),context.getString(R.string.default_image_quality_value))!!))
            .placeholder(R.drawable.ic_weather_app_icon)
            .into(holder.binding.imageViewCondition)
    }

    private fun getItemAt(adapterPosition: Int): DailyForecast = getItem(adapterPosition)

    class WeatherDayViewHolder(var binding:ItemWeatherDailyBinding, private var clickListener:AdapterItemClickListener<DailyForecast>?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(d: DailyForecast){
            binding.run {
                dailyForecast = d
                root.setOnClickListener {
                    clickListener?.onItemClick(d,adapterPosition)
                }
            }
        }
    }
}

