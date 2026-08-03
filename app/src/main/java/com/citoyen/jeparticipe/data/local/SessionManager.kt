package com.citoyen.jeparticipe.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NOM = "user_nom"
        private const val KEY_USER_PRENOM = "user_prenom"
        private const val KEY_FIREBASE_TOKEN = "firebase_token"
    }

    // ============ TOKEN ============
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // ============ ROLE ============
    fun saveUserRole(role: String) {
        val cleanRole = role.replace("ROLE_", "")
        prefs.edit().putString(KEY_USER_ROLE, cleanRole).apply()
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    // ============ ID ============
    fun saveUserId(id: Long) {
        prefs.edit().putLong(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    // ============ EMAIL ============
    fun saveUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    // ============ NOM ============
    fun saveUserNom(nom: String) {
        prefs.edit().putString(KEY_USER_NOM, nom).apply()
    }

    fun getUserNom(): String? {
        return prefs.getString(KEY_USER_NOM, null)
    }

    // ============ PRENOM ============
    fun saveUserPrenom(prenom: String) {
        prefs.edit().putString(KEY_USER_PRENOM, prenom).apply()
    }

    fun getUserPrenom(): String? {
        return prefs.getString(KEY_USER_PRENOM, null)
    }

    // ============ FIREBASE TOKEN ============
    fun saveFirebaseToken(token: String) {
        prefs.edit().putString(KEY_FIREBASE_TOKEN, token).apply()
    }

    fun getFirebaseToken(): String? {
        return prefs.getString(KEY_FIREBASE_TOKEN, null)
    }

    // ============ UTILITAIRES ============
    // ✅ UNE SEULE FOIS isLoggedIn()
    fun isLoggedIn(): Boolean {
        val token = getToken()
        return token != null && token.isNotEmpty()
    }

    fun hasRole(role: String): Boolean {
        val userRole = getUserRole()
        return userRole?.equals(role, ignoreCase = true) == true
    }

    fun isCitoyen(): Boolean {
        return hasRole("CITOYEN")
    }

    fun isServicePublic(): Boolean {
        return hasRole("SERVICE_PUBLIC")
    }

    fun isAdmin(): Boolean {
        return hasRole("ADMIN")
    }

    // ============ DECONNEXION ============
    fun clearSession() {
        prefs.edit().clear().apply()
        println("🔴 Session effacée - Token: ${getToken()}")
    }
}