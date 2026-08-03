package com.citoyen.jeparticipe.ui.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.citoyen.jeparticipe.data.repository.AuthRepository
import com.citoyen.jeparticipe.data.repository.SignalementRepository

class AgentSignalementViewModelFactory(
    private val repository: SignalementRepository,
    private val authRepository: AuthRepository   // ✅ AJOUT
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgentSignalementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgentSignalementViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}