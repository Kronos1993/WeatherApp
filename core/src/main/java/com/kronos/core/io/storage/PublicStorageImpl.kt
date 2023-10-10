package com.kronos.core.io.storage

import android.content.Context
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class PublicStorageImpl @Inject constructor(@ApplicationContext val context: Context) :
    Storage {
    override fun getPath(): File? {

        val path = File(
            ContextCompat.getExternalFilesDirs(
                context,
                null
            )[0].absolutePath
        )
        return path.extractBaseExternalPath()?.let { File(it) }
    }

    override fun getFallbackPath(): File? = null
}

