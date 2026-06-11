package com.example.mathia

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MathiaMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        println("FCM TOKEN: $token")
    }

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        super.onMessageReceived(remoteMessage)

        val title =
            remoteMessage.notification?.title
                ?: "MathIA"

        val body =
            remoteMessage.notification?.body
                ?: "Tienes una nueva notificación"

        mostrarNotificacion(
            title,
            body
        )
    }

    private fun mostrarNotificacion(
        titulo: String,
        mensaje: String
    ) {

        val channelId =
            "mathia_notifications"

        val channelName =
            "Notificaciones MathIA"

        val notificationManager =
            getSystemService(
                NotificationManager::class.java
            )

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.description =
                "Canal de notificaciones de MathIA"

            notificationManager
                .createNotificationChannel(
                    channel
                )
        }

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    titulo
                )
                .setContentText(
                    mensaje
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(
                    true
                )
                .setContentIntent(
                    pendingIntent
                )
                .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}