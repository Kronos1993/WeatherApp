package com.kronos.weatherapp.ui.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.domian.model.UserCustomLocation
import com.kronos.weatherapp.databinding.ItemUserLocationBinding
import com.kronos.weatherapp.databinding.ItemWeatherDailyBinding
import com.kronos.webclient.UrlProvider

class UserLocationAdapter : ListAdapter<UserCustomLocation, UserLocationAdapter.UserLocationViewHolder>(GeneralDiffCallback<UserCustomLocation>()) {

    private var adapterItemClickListener:AdapterItemClickListener<UserCustomLocation>?=null
    private lateinit var urlProvider: UrlProvider

    fun setAdapterItemClick(adapterItemClickListener:AdapterItemClickListener<UserCustomLocation>?){
        this.adapterItemClickListener = adapterItemClickListener
    }

    fun setUrlProvider(urlProvider: UrlProvider){
        this.urlProvider = urlProvider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLocationViewHolder {
        val binding = ItemUserLocationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserLocationViewHolder(binding,adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: UserLocationViewHolder, position: Int) {
        val current = getItemAt(position)
        holder.bind(current,position)
    }

    fun getItemAt(adapterPosition: Int): UserCustomLocation = getItem(adapterPosition)

    class UserLocationViewHolder(var binding:ItemUserLocationBinding, var clickListener:AdapterItemClickListener<UserCustomLocation>?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(d: UserCustomLocation,position: Int){
            binding.run {
                userLocation = d
                root.setOnClickListener {
                    clickListener?.onItemClick(d,adapterPosition)
                }
            }
        }
    }
}

