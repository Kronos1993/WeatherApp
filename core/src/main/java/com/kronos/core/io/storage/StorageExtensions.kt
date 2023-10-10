package com.kronos.core.io.storage

import java.io.File

fun File.extractBaseExternalPath(): String? {
    val fullPath = this.absolutePath
    var maxLength = fullPath.indexOf("Android")
    maxLength = if (maxLength > 0) maxLength else fullPath.length
    return fullPath.substring(0, maxLength)
}

