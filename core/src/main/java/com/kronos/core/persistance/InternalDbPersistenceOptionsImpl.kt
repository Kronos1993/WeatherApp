package com.kronos.core.persistance

import com.kronos.core.io.PersistenceOptions
import com.kronos.core.io.di.PublicStorage
import com.kronos.core.io.storage.Storage
import javax.inject.Inject

class InternalDbPersistenceOptionsImpl  @Inject constructor(
    @PublicStorage  override val storage: Storage,
) : PersistenceOptions {

    override val basePath: String
        get() = "my-box/db"
}