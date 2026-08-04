package com.citoyen.jeparticipe.ui.admin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citoyen.jeparticipe.data.model.Commentaire
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.data.model.User
import com.citoyen.jeparticipe.data.model.RegisterRequest
import com.citoyen.jeparticipe.data.repository.SignalementRepository
import com.citoyen.jeparticipe.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AdminStats(
    val totalSignalements: Int = 0,
    val enAttente: Int = 0,
    val enCours: Int = 0,
    val resolus: Int = 0,
    val totalUsers: Int = 0
)

class AdminViewModel(
    private val repository: SignalementRepository,
    private val authRepository: AuthRepository  // ✅ Injection
) : ViewModel() {

    val signalements = mutableStateListOf<Signalement>()
    val users = mutableStateListOf<User>()
    val agents = mutableStateListOf<User>()
    val isLoadingSignalements = mutableStateOf(false)
    val isLoadingUsers = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    // ============ COMMENTAIRES ============
    val commentaires = mutableStateListOf<Commentaire>()
    val isLoadingAgents = mutableStateOf(false)
    val isLoadingCommentaires = mutableStateOf(false)
    val selectedSignalementId = mutableStateOf<Long?>(null)

    // Assignation et liste agents
    // ✅ NOUVEAU : charger les agents
    fun chargerAgents() {
        viewModelScope.launch {
            isLoadingAgents.value = true
            try {
                val result = repository.getAgents()
                agents.clear()
                agents.addAll(result)
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoadingAgents.value = false
            }
        }
    }

    // ✅ NOUVEAU : assigner un agent à un signalement
    fun assignerAgent(signalement: Signalement, agentId: Long) {
        viewModelScope.launch {
            try {
                val id = signalement.id ?: return@launch
                val updated = repository.assignerAgent(id, agentId)
                val index = signalements.indexOfFirst { it.id == id }
                if (index != -1) signalements[index] = updated
                successMessage.value = "✅ Agent assigné avec succès"
                chargerSignalements()
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    // ============ SIGNALEMENTS ============
    fun chargerSignalements() {
        viewModelScope.launch {
            isLoadingSignalements.value = true
            errorMessage.value = null
            try {
                val result = repository.getAllSignalements()
                signalements.clear()
                signalements.addAll(result)
                println("✅ Signalements chargés: ${result.size}")
            } catch (e: Exception) {
                errorMessage.value = e.message
                println("❌ Erreur chargement signalements: ${e.message}")
            } finally {
                isLoadingSignalements.value = false
            }
        }
    }

    fun updateStatut(signalement: Signalement, nouveauStatut: String) {
        viewModelScope.launch {
            try {
                val id = signalement.id
                if (id != null) {
                    val updated = repository.updateStatut(id, nouveauStatut)
                    val index = signalements.indexOfFirst { it.id == id }
                    if (index != -1) {
                        signalements[index] = updated
                    }
                    successMessage.value = "✅ Statut mis à jour"
                    chargerSignalements()
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    // ✅ Supprimer un signalement
    fun deleteSignalement(signalement: Signalement) {
        viewModelScope.launch {
            try {
                val id = signalement.id
                if (id != null) {
                    repository.deleteSignalement(id)
                    signalements.removeAll { it.id == id }
                    successMessage.value = "✅ Signalement supprimé avec succès"
                    chargerSignalements()
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    // ============ UTILISATEURS ============
    fun chargerUtilisateurs() {
        viewModelScope.launch {
            isLoadingUsers.value = true
            errorMessage.value = null
            try {
                val result = repository.getAllUsers()
                users.clear()
                users.addAll(result)
                println("✅ Utilisateurs chargés: ${result.size}")
            } catch (e: Exception) {
                errorMessage.value = e.message
                println("❌ Erreur chargement utilisateurs: ${e.message}")
            } finally {
                isLoadingUsers.value = false
            }
        }
    }

    fun createUser(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        telephone: String?
    ) {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    nom = nom.trim(),
                    prenom = prenom.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                    telephone = telephone?.trim()?.takeIf { it.isNotEmpty() }
                )
                val response = authRepository.register(request)
                if (response.success) {
                    successMessage.value = "✅ Utilisateur créé avec succès"
                    chargerUtilisateurs()
                } else {
                    errorMessage.value = response.message ?: "Erreur lors de la création"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun updateRole(user: User, nouveauRole: String) {
        viewModelScope.launch {
            try {
                val id = user.id
                if (id != null) {
                    val updated = repository.updateUserRole(id, nouveauRole)
                    val index = users.indexOfFirst { it.id == id }
                    if (index != -1) {
                        users[index] = updated
                    }
                    successMessage.value = "✅ Rôle changé: $nouveauRole"
                    chargerUtilisateurs()
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun toggleUserStatus(user: User) {
        viewModelScope.launch {
            try {
                val id = user.id
                if (id != null) {
                    val updated = repository.updateUserStatus(id)
                    val index = users.indexOfFirst { it.id == id }
                    if (index != -1) {
                        users[index] = updated
                    }
                    successMessage.value = if (updated.actif == true) "✅ Utilisateur activé" else "⛔ Utilisateur désactivé"
                    chargerUtilisateurs()
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                val id = user.id
                if (id != null) {
                    repository.deleteUser(id)
                    users.removeAll { it.id == id }
                    successMessage.value = "✅ Utilisateur supprimé"
                    chargerUtilisateurs()
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    // ✅ Changer le mot de passe - Utiliser authRepository
    fun changePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔐 Tentative de changement de mot de passe...")

                // ✅ Utiliser authRepository (qui a le bon client avec token)
                val result = authRepository.changePassword(oldPassword, newPassword)

                if (result) {
                    println("✅ Changement de mot de passe réussi")
                    onSuccess()
                } else {
                    onError("Ancien mot de passe incorrect ou erreur serveur")
                }
            } catch (e: HttpException) {
                val errorCode = e.code()
                val errorMessage = when (errorCode) {
                    400 -> "Ancien mot de passe incorrect"
                    401 -> "Session expirée, veuillez vous reconnecter"
                    403 -> "Vous n'êtes pas autorisé à changer le mot de passe"
                    404 -> "Utilisateur non trouvé"
                    else -> "Erreur serveur ($errorCode)"
                }
                println("❌ Erreur HTTP $errorCode: $errorMessage")
                onError(errorMessage)
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
                onError(e.message ?: "Erreur lors du changement de mot de passe")
            }
        }
    }

    // ============ COMMENTAIRES ============
    fun chargerCommentaires(signalementId: Long) {
        viewModelScope.launch {
            isLoadingCommentaires.value = true
            selectedSignalementId.value = signalementId
            try {
                val result = repository.getCommentaires(signalementId)
                commentaires.clear()
                commentaires.addAll(result.map { it.copy(signalementId = signalementId) })
                println("✅ Commentaires chargés: ${result.size}")
            } catch (e: Exception) {
                errorMessage.value = e.message
                println("❌ Erreur chargement commentaires: ${e.message}")
            } finally {
                isLoadingCommentaires.value = false
            }
        }
    }

    fun ajouterCommentaire(signalementId: Long, contenu: String, estJustification: Boolean = false) {
        viewModelScope.launch {
            try {
                val commentaire = repository.ajouterCommentaire(signalementId, contenu, estJustification)
                commentaires.add(0, commentaire.copy(signalementId = signalementId))
                successMessage.value = "✅ Commentaire ajouté"
                println("✅ Commentaire ajouté")
            } catch (e: Exception) {
                errorMessage.value = e.message
                println("❌ Erreur ajout commentaire: ${e.message}")
            }
        }
    }

    fun supprimerCommentaire(commentaireId: Long) {
        viewModelScope.launch {
            try {
                repository.supprimerCommentaire(commentaireId)
                commentaires.removeAll { it.id == commentaireId }
                successMessage.value = "✅ Commentaire supprimé"
                println("✅ Commentaire supprimé")
            } catch (e: Exception) {
                errorMessage.value = e.message
                println("❌ Erreur suppression commentaire: ${e.message}")
            }
        }
    }

    fun marquerCommeLu(commentaireId: Long) {
        viewModelScope.launch {
            try {
                repository.marquerCommeLu(commentaireId)
                val index = commentaires.indexOfFirst { it.id == commentaireId }
                if (index != -1) {
                    val updated = commentaires[index].copy(lu = true)
                    commentaires[index] = updated
                }
                println("✅ Commentaire marqué comme lu")
            } catch (e: Exception) {
                println("❌ Erreur lors du marquage: ${e.message}")
            }
        }
    }

    fun marquerTousCommeLus(signalementId: Long) {
        viewModelScope.launch {
            try {
                repository.marquerTousCommeLus(signalementId)
                for (i in commentaires.indices) {
                    commentaires[i] = commentaires[i].copy(lu = true)
                }
                println("✅ Tous les commentaires marqués comme lus")
            } catch (e: Exception) {
                println("❌ Erreur lors du marquage: ${e.message}")
            }
        }
    }

    fun getStats(): AdminStats {
        return AdminStats(
            totalSignalements = signalements.size,
            enAttente = signalements.count { it.statut?.uppercase() == "EN_ATTENTE" },
            enCours = signalements.count { it.statut?.uppercase() == "EN_COURS" },
            resolus = signalements.count { it.statut?.uppercase() == "RESOLU" },
            totalUsers = users.size
        )
    }
}