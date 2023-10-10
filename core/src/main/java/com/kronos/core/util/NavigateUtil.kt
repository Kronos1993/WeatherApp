package com.kronos.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

fun navigate(context: Context, clazz: Class<*>?) {
    val intentHome = Intent(context, clazz)
    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intentHome)
}

fun startActivityForResult(context: Activity, action: String, requestCode: Int) {
    context.startActivityForResult(Intent(action), requestCode)
}

fun navigate(context: Context, clazz: Class<*>?, key: String?, extra: String?) {
    val intentHome = Intent(context, clazz)
    intentHome.putExtra(key, extra)
    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    context.startActivity(intentHome)
}

fun navigate(context: Context, clazz: Class<*>?, extra: Bundle?) {
    val intentHome = Intent(context, clazz)
    intentHome.putExtras(extra!!)
    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intentHome)
}

fun navigate(context: Context, clazz: Class<*>?, flag: Int) {
    val intentHome = Intent(context, clazz)
    intentHome.addFlags(flag)
    context.startActivity(intentHome)
}