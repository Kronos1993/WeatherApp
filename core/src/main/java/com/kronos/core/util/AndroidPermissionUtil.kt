package com.kronos.core.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


fun validatePermission(context: Context, perm: String?): Boolean {
    var permission_granted = false
    if (ActivityCompat.checkSelfPermission(
            context,
            perm!!
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        permission_granted = true
    }
    return permission_granted
}