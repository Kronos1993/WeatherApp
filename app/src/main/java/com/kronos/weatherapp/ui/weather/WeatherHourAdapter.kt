package com.kronos.weatherapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.core.util.PreferencesUtil
import com.kronos.domian.model.Hour
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.ItemWeatherHourBinding
import com.kronos.webclient.UrlProvider

class WeatherHourAdapter : ListAdapter<Hour, WeatherHourAdapter.WeatherHourViewHolder>(GeneralDiffCallback<Hour>()) {

    private var adapterItemClickListener:AdapterItemClickListener<Hour>?=null
    private lateinit var urlProvider: UrlProvider

    fun setAdapterItemClick(adapterItemClickListener:AdapterItemClickListener<Hour>?){
        this.adapterItemClickListener = adapterItemClickListener
    }

    fun setUrlProvider(urlProvider: UrlProvider){
        this.urlProvider = urlProvider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHourViewHolder {
        val binding = ItemWeatherHourBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WeatherHourViewHolder(binding,adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: WeatherHourViewHolder, position: Int) {
        val current = getItemAt(position)
        holder.bind(current)
        val context = holder.binding.imageViewCurrentWeather.context
        Glide.with(holder.binding.imageViewCurrentWeather)
            .load(urlProvider.getImageUrl(current.condition.icon,PreferencesUtil.getPreference(context,context.getString(R.string.default_image_quality_key),context.getString(R.string.default_image_quality_value))!!))
            .placeholder(R.drawable.ic_weather_app_icon)
            .into(holder.binding.imageViewCurrentWeather)
    }

    private fun getItemAt(adapterPosition: Int): Hour = getItem(adapterPosition)

    class WeatherHourViewHolder(var binding:ItemWeatherHourBinding, private var clickListener:AdapterItemClickListener<Hour>?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(h: Hour){
            binding.run {
                hour = h
                root.setOnClickListener {
                    clickListener?.onItemClick(h,adapterPosition)
                }
            }
        }
    }
}

