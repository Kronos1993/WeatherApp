package com.kronos.domian.model

data class UserCustomLocation(
    var id:Long = 0,
    var cityName:String = "",
    var isCurrent:Boolean = false,
    var isSelected:Boolean = false,
    var lat:Double? = 0.0,
    var lon:Double? = 0.0
)
