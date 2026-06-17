package com.example.mathia

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "mathia_daily_alarm"
        val channelName = "Recordatorios Diarios MathIA"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para mantener la racha de estudio"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Select a random message to make it feel dynamic
        val titles = listOf(
            "¡Tu racha corre peligro!",
            "Mateo te está esperando...",
            "¡Nuevos artículos en la tiendita!",
            "¡Entrenamiento matemático del día!"
        )
        val messages = listOf(
            "Entra a MathIA y completa tu práctica diaria para no perder tu racha.",
            "Ven a resolver divertidos desafíos y acumula estrellas para comprar cosméticos.",
            "¡Tus estrellas acumuladas quieren ser gastadas! Ven a ver los nuevos avatares.",
            "Solo te tomará 3 minutos mantener tu cerebro en forma hoy."
        )

        val index = (titles.indices).random()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use default launcher icon
            .setContentTitle(titles[index])
            .setContentText(messages[index])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(2002, notification)
    }
}
