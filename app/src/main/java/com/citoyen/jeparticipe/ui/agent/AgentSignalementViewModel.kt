package com.citoyen.jeparticipe.ui.agent

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citoyen.jeparticipe.data.model.Commentaire
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.data.repository.AuthRepository
import com.citoyen.jeparticipe.data.repository.SignalementRepository
import com.citoyen.jeparticipe.utils.DateUtils
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AgentSignalementViewModel(
    private val repository: SignalementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val signalements = mutableStateListOf<Signalement>()
    private val allSignalements = mutableStateListOf<Signalement>()
    val isLoading = mutableStateOf(false)

    // État pour la recherche
    var searchQuery = mutableStateOf("")

    // État pour le tri (true = plus récent d'abord)
    var sortByDate = mutableStateOf(true)

    // ============ COMMENTAIRES ============
    val commentaires = mutableStateListOf<Commentaire>()
    val isLoadingCommentaires = mutableStateOf(false)
    val selectedSignalementId = mutableStateOf<Long?>(null)

    fun chargerSignalements() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = repository.getAllSignalements()
                allSignalements.clear()
                allSignalements.addAll(result)
                applyFiltersAndSort()
            } catch (e: Exception) {
                println("Erreur: ${e.message}")
            } finally {
                isLoading.value = false
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
                    chargerSignalements()
                } else {
                    println("⚠️ ID du signalement est null")
                }
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
            }
        }
    }

    // ============ MÉTHODES COMMENTAIRES ============
    fun chargerCommentaires(signalementId: Long) {
        viewModelScope.launch {
            isLoadingCommentaires.value = true
            selectedSignalementId.value = signalementId
            try {
                val result = repository.getCommentaires(signalementId)
                commentaires.clear()
                commentaires.addAll(result)
            } catch (e: Exception) {
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
                commentaires.add(0, commentaire)
                println("✅ Commentaire ajouté")
            } catch (e: Exception) {
                println("❌ Erreur ajout commentaire: ${e.message}")
            }
        }
    }

    fun supprimerCommentaire(commentaireId: Long) {
        viewModelScope.launch {
            try {
                repository.supprimerCommentaire(commentaireId)
                commentaires.removeAll { it.id == commentaireId }
                println("✅ Commentaire supprimé")
            } catch (e: Exception) {
                println("❌ Erreur suppression commentaire: ${e.message}")
            }
        }
    }

    // ============ BADGES NON LUS ============
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

// ... dans la classe, après les autres fonctions

    // ✅ CHANGER LE MOT DE PASSE
    fun changePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = authRepository.changePassword(oldPassword, newPassword)
                if (result) {
                    onSuccess()
                } else {
                    onError("Ancien mot de passe incorrect ou erreur serveur")
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    400 -> "Ancien mot de passe incorrect"
                    401 -> "Session expirée, veuillez vous reconnecter"
                    403 -> "Vous n'êtes pas autorisé à changer le mot de passe"
                    404 -> "Utilisateur non trouvé"
                    else -> "Erreur serveur (${e.code()})"
                }
                onError(errorMessage)
            } catch (e: Exception) {
                onError(e.message ?: "Erreur lors du changement de mot de passe")
            }
        }
    }

    // Appliquer les filtres et le tri
    private fun applyFiltersAndSort() {
        var list = allSignalements.toList()

        // Recherche par titre ou description
        if (searchQuery.value.isNotBlank()) {
            list = list.filter {
                it.titre.contains(searchQuery.value, ignoreCase = true) ||
                        it.description?.contains(searchQuery.value, ignoreCase = true) == true
            }
        }

        // Tri par date (plus récent en premier)
        if (sortByDate.value) {
            list = DateUtils.sortByDateDesc(list)
        }

        signalements.clear()
        signalements.addAll(list)
    }

    // Fonction de recherche
    fun search(query: String) {
        searchQuery.value = query
        applyFiltersAndSort()
    }

    // Fonction pour changer le tri
    fun toggleSort() {
        sortByDate.value = !sortByDate.value
        applyFiltersAndSort()
    }
}