package com.kronos.core.io.storage

import java.io.File
import javax.inject.Inject

class AbsoluteStorageImpl @Inject constructor(val path: String) : Storage {
    override fun getPath(): File? {
        return File(path)
    }

    override fun getFallbackPath(): File? = null
}