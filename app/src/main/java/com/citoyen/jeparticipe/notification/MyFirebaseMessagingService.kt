package com.citoyen.jeparticipe.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.citoyen.jeparticipe.MainActivity
import com.citoyen.jeparticipe.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "je_participe_channel"
        private const val CHANNEL_NAME = "JeParticipe Notifications"
        private const val NOTIFICATION_ID = 1000
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extraire les données de la notification
        val title = remoteMessage.notification?.title ?: "JeParticipe"
        val body = remoteMessage.notification?.body ?: "Vous avez une nouvelle notification"

        // Extraire les données personnalisées
        val signalementId = remoteMessage.data["signalementId"]?.toLongOrNull()
        val type = remoteMessage.data["type"] ?: "info"

        // Afficher la notification
        showNotification(title, body, signalementId, type)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Envoyer le nouveau token au serveur
        sendTokenToServer(token)
    }

    private fun showNotification(title: String, body: String, signalementId: Long? = null, type: String = "info") {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_type", type)
            putExtra("signalement_id", signalementId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Créer le canal de notification (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour JeParticipe"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendTokenToServer(token: String) {
        // Envoyer le token au serveur via votre repository
        // Cette fonction sera appelée depuis le ViewModel
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("fcm_token", token).apply()

        // Si l'utilisateur est connecté, envoyer le token au serveur
        val sessionManager = com.citoyen.jeparticipe.data.local.SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            // TODO: Envoyer le token au backend via une API
            println("FCM Token: $token")
        }
    }
}