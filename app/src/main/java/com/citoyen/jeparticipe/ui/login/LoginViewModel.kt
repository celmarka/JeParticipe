package com.citoyen.jeparticipe.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citoyen.jeparticipe.data.local.SessionManager
import com.citoyen.jeparticipe.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Veuillez remplir tous les champs")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = authRepository.login(email, password)
                _uiState.value = LoginUiState.Success(response.role)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erreur de connexion")
            }
        }
    }

    fun enregistrerTokenFCM(token: String) {
        viewModelScope.launch {
            try {
                println("📤 Appel de authRepository.enregistrerTokenFCM()...")
                val result = authRepository.enregistrerTokenFCM(token)
                if (result) {
                    println("✅ Token FCM enregistré avec succès dans le backend")
                } else {
                    println("❌ Échec de l'enregistrement du token FCM")
                }
            } catch (e: Exception) {
                println("❌ Erreur enregistrement token FCM: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}