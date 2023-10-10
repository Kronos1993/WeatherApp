package com.kronos.domian.model

data class Response<T>(
    var data:T?,
    var ex:Throwable?,
    var errors:List<String> = listOf(),
    var code:Int = 0
)
