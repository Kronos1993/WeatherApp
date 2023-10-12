package com.kronos.domian.model

data class Response<T>(
    var data:T? = null,
    var ex:Throwable? = null,
    var errors:List<String> = listOf(),
    var code:Int = 0
)
