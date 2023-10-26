package com.kronos.core.util

import android.appwidget.AppWidgetManager

import android.content.ComponentName
import android.content.Context

import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


fun updateWidget(context: Context,widgetClass: Class<*>) {
    runBlocking(Dispatchers.IO){
        val intent = Intent(context, widgetClass)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, widgetClass!!))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }
}