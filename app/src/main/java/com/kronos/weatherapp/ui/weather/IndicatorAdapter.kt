package com.kronos.weatherapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.ItemIndicatorBinding
import com.kronos.weatherapp.ui.weather.model.Indicator
import com.kronos.webclient.UrlProvider

class IndicatorAdapter : ListAdapter<Indicator, IndicatorAdapter.IndicatorViewHolder>(GeneralDiffCallback<Indicator>()) {

    private var adapterItemClickListener:AdapterItemClickListener<Indicator>?=null
    private lateinit var urlProvider: UrlProvider

    fun setAdapterItemClick(adapterItemClickListener:AdapterItemClickListener<Indicator>?){
        this.adapterItemClickListener = adapterItemClickListener
    }

    fun setUrlProvider(urlProvider: UrlProvider){
        this.urlProvider = urlProvider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
        val binding = ItemIndicatorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IndicatorViewHolder(binding,adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
        val current = getItemAt(position)
        holder.bind(current)
        Glide.with(holder.binding.appCompatImageView)
            .load(current.image)
            .placeholder(R.drawable.ic_weather_app_icon)
            .into(holder.binding.appCompatImageView)
    }

    private fun getItemAt(adapterPosition: Int): Indicator = getItem(adapterPosition)

    class IndicatorViewHolder(var binding:ItemIndicatorBinding, private var clickListener:AdapterItemClickListener<Indicator>?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(h: Indicator){
            binding.run {
                indicator = h
                root.setOnClickListener {
                    clickListener?.onItemClick(h,adapterPosition)
                }
            }
        }
    }
}

