package com.citoyen.jeparticipe.notification

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseTokenManager(private val context: Context) {

    private val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPref.getString("fcm_token", null)
    }

    fun saveToken(token: String) {
        sharedPref.edit().putString("fcm_token", token).apply()
    }

    fun refreshToken(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    token?.let {
                        saveToken(it)
                        onSuccess(it)
                    }
                } else {
                    onError("Erreur lors de la récupération du token FCM")
                }
            }
    }
}