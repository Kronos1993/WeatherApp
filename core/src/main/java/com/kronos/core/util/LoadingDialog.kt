package com.kronos.core.util

import android.content.Context
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import java.lang.ref.WeakReference

fun getProgressDialog(context: Context, text: Int, progressColor: Int): SweetAlertDialog {
    val progressDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    progressDialog.progressHelper?.barColor =
        ContextCompat.getColor(context, progressColor)
    progressDialog.setTitle(text)
    progressDialog.setCancelable(false)
    return progressDialog
}