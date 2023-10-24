package com.kronos.core.notification

import android.content.Context
import android.graphics.Bitmap

interface INotifications {

    fun createNotification(title:String ,  description:String, group:String , notificationsId:NotificationType, iconDrawable:Int, context:Context,notificationImage:Bitmap? = null)
    fun hideNotification(notificationType: NotificationType,context:Context)

}