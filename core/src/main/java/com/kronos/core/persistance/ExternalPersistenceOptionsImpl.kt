package com.kronos.core.persistance

import com.kronos.core.io.PersistenceOptions
import com.kronos.core.io.di.ExternalStorage
import com.kronos.core.io.storage.Storage
import javax.inject.Inject

class ExternalPersistenceOptionsImpl @Inject constructor(
    @ExternalStorage override val storage: Storage,
) : PersistenceOptions {

    override val basePath: String
        get() = "my-box"
}