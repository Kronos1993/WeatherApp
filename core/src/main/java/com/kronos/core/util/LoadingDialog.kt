package com.kronos.core.util

import android.content.Context
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import java.lang.ref.WeakReference

class LoadingDialog {

    companion object {
        var progressDialog:WeakReference<SweetAlertDialog>? = null
        fun getProgressDialog(context: Context, text: Int, progressColor: Int): SweetAlertDialog? {
            if (progressDialog?.get() == null) {
                var dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                progressDialog = WeakReference(dialog)
                progressDialog?.get()?.progressHelper?.barColor =
                    ContextCompat.getColor(context, progressColor)
                progressDialog?.get()?.setTitle(text)
                progressDialog?.get()?.setCancelable(false)
            }
            return progressDialog!!.get()
        }
    }
}