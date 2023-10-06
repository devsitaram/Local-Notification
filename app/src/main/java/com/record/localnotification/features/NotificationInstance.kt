package com.record.localnotification.features

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.record.localnotification.R

object NotificationInstance {
    fun showNotification(context: Context, description: String) {
        val notification = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hello world")
            .setContentText(description)
            .build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}