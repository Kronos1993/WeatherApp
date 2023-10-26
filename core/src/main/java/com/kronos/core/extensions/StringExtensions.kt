package com.kronos.core.extensions

fun String.capitalizeFirstLetter():String{
    return this.replaceFirstChar { i-> i.uppercaseChar() }
}