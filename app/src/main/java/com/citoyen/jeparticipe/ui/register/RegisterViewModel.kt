package com.citoyen.jeparticipe.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citoyen.jeparticipe.data.model.RegisterRequest
import com.citoyen.jeparticipe.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val message: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        confirmPassword: String,
        telephone: String?
    ) {
        // Validations
        if (nom.isBlank() || prenom.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = RegisterUiState.Error("Tous les champs sont obligatoires")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = RegisterUiState.Error("Les mots de passe ne correspondent pas")
            return
        }

        if (password.length < 6) {
            _uiState.value = RegisterUiState.Error("Le mot de passe doit contenir au moins 6 caractères")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = RegisterUiState.Error("Email invalide")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            try {
                val request = RegisterRequest(
                    nom = nom.trim(),
                    prenom = prenom.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                    telephone = telephone?.trim()?.takeIf { it.isNotEmpty() }
                )

                println("📝 RegisterRequest: $request") // Log pour déboguer

                val response = authRepository.register(request)

                println("📝 RegisterResponse: $response") // Log pour déboguer

                if (response.success) {
                    _uiState.value = RegisterUiState.Success(
                        response.message ?: "✅ Inscription réussie !"
                    )
                } else {
                    _uiState.value = RegisterUiState.Error(
                        response.message ?: "❌ Erreur lors de l'inscription"
                    )
                }
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
                _uiState.value = RegisterUiState.Error(
                    e.message ?: "❌ Erreur lors de l'inscription"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}