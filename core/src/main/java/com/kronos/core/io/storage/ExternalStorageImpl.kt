package com.kronos.core.io.storage

import android.content.Context
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ExternalStorageImpl @Inject constructor(
    @ApplicationContext val context: Context
) : Storage {
    override fun getPath(): File? {
        val externalDirs = ContextCompat.getExternalFilesDirs(context, null)
        return if (externalDirs.size > 1) {
            externalDirs[1].extractBaseExternalPath()?.let { File(it) }
        } else {
            File("")
        }
    }

    override fun getFallbackPath(): File? {
        return null
    }

}
