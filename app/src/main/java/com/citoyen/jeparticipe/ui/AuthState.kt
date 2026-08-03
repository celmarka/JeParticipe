package com.citoyen.jeparticipe.ui

import androidx.compose.runtime.compositionLocalOf

data class AuthState(
    val isLoggedIn: Boolean = false,
    val userRole: String? = null
)

// CompositionLocal pour partager l'état d'authentification
val LocalAuthState = compositionLocalOf { AuthState() }