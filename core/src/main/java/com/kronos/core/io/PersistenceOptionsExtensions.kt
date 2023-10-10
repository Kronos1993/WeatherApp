package com.kronos.core.io

import java.io.File

fun PersistenceOptions.getBasePath(): File {
    requireNotNull(this) { "persistentOption is null" }

    requireNotNull(this.storage) { "storage is null" }

    val basePath: File =
        File(this.storage.getPath(), this.basePath)
    if (!basePath.exists()) {
        try {
            basePath.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return basePath
}

fun PersistenceOptions.getFile(fileName: String): File {
    return File(this.getBasePath(), fileName)
}
