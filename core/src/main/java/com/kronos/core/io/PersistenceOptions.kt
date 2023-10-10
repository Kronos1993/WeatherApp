package com.kronos.core.io

import com.kronos.core.io.storage.Storage


interface PersistenceOptions {
    val basePath: String
    val storage: Storage
}