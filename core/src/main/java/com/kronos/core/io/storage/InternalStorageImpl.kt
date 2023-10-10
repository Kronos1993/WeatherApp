package com.kronos.core.io.storage

import android.os.Environment
import com.kronos.core.io.storage.Storage
import java.io.File
import javax.inject.Inject

class InternalStorageImpl @Inject constructor() : Storage {
    override fun getPath(): File? {
        return Environment.getDataDirectory()
    }

    override fun getFallbackPath(): File? = null
}