package com.kronos.core.io.storage

import java.io.File

interface Storage {
    fun getPath(): File?
    fun getFallbackPath(): File?
}