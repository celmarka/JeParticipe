package com.citoyen.jeparticipe.ui.citoyen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citoyen.jeparticipe.data.repository.AuthRepository
import com.citoyen.jeparticipe.data.repository.SignalementRepository

class SignalementViewModelFactory(
    private val repository: SignalementRepository,
    private val authRepository: AuthRepository   // ✅ AJOUT
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignalementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignalementViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}