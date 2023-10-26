package com.kronos.core.adapters.diff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class GeneralDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}