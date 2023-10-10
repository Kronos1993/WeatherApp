package com.kronos.core.adapters

interface AdapterItemClickListener<T> {
    fun onItemClick(t:T,pos:Int)
}