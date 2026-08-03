package com.citoyen.jeparticipe.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citoyen.jeparticipe.data.local.SessionManager
import com.citoyen.jeparticipe.data.repository.AuthRepository

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository, sessionManager) as T  // ✅ Deux paramètres
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}