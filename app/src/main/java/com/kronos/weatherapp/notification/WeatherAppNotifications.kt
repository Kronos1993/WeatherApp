package com.kronos.weatherapp.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationGroup
import com.kronos.core.notification.NotificationType
import com.kronos.weatherapp.MainActivity
import com.kronos.weatherapp.NOTIFICATION_CHANNEL
import javax.inject.Inject


class WeatherAppNotifications @Inject constructor() : INotifications {
    override fun createNotification(
        title: String,
        shortDescription:String,
        longDescription: String,
        group: String,
        notificationsId: NotificationType,
        iconDrawable: Int,
        context: Context,
        notificationImage:Bitmap?
    ) {
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            var intent = Intent(context, MainActivity::class.java)

            val pendingIntent: PendingIntent? =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)

            val notificationManager: NotificationManagerCompat =
                NotificationManagerCompat.from(context)
            val notification: Notification =
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                    .setSmallIcon(iconDrawable)
                    .setContentTitle(title)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(longDescription)
                            .setBigContentTitle(title)
                    )
                    .setContentText(shortDescription)
                    .setLargeIcon(notificationImage)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setGroup(group)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .build()
            notificationManager.notify(notificationsId.ordinal, notification)
        }
    }

    override fun hideNotification(notificationType: NotificationType, context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationType.ordinal)
    }
}