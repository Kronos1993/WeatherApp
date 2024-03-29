package com.kronos.webclient

interface UrlProvider {
    fun getApiUrl():String
    fun getServerUrl():String
    fun getImageUrl(partUrl:String,quality:String):String
    fun extractIdFromUrl(url:String):Int
}