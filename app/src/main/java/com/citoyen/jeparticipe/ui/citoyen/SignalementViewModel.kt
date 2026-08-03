package com.citoyen.jeparticipe.ui.citoyen

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

class SignalementViewModel(
    private val repository: SignalementRepository,
    private val authRepository: AuthRepository   // ✅ AJOUT
) : ViewModel() {

    val mesSignalements = mutableStateListOf<Signalement>()
    private val allSignalements = mutableStateListOf<Signalement>()

    var searchQuery = mutableStateOf("")
    var sortByDate = mutableStateOf(true)

    // ============ COMMENTAIRES ============
    val commentaires = mutableStateListOf<Commentaire>()
    val isLoadingCommentaires = mutableStateOf(false)
    val selectedSignalementId = mutableStateOf<Long?>(null)

    // ============ CRÉATION AVEC PHOTO ============
    fun createSignalementWithPhoto(
        titre: String,
        description: String,
        latitude: Double?,
        longitude: Double?,
        adresse: String,
        categorie: String,
        photoBase64: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createSignalement(
                    titre = titre,
                    description = description,
                    latitude = latitude,
                    longitude = longitude,
                    adresse = adresse,
                    categorie = categorie,
                    photoBase64 = photoBase64
                )
                println("Signalement créé : $response")
                chargerMesSignalements()
                onSuccess()
            } catch (e: Exception) {
                println("Erreur : ${e.message}")
                onError(e.message ?: "Erreur lors de la création du signalement")
            }
        }
    }

    // ============ CRÉATION SANS PHOTO (pour compatibilité) ============
    fun createSignalement(signalement: Signalement) {
        viewModelScope.launch {
            try {
                val response = repository.createSignalement(
                    titre = signalement.titre,
                    description = signalement.description ?: "",
                    latitude = signalement.latitude,
                    longitude = signalement.longitude,
                    adresse = signalement.adresse ?: "",
                    categorie = signalement.categorie ?: "",
                    photoBase64 = null
                )
                println("Signalement créé : $response")
                chargerMesSignalements()
            } catch (e: Exception) {
                println("Erreur : ${e.message}")
            }
        }
    }

    fun chargerMesSignalements() {
        viewModelScope.launch {
            try {
                val result = repository.getMesSignalements()
                allSignalements.clear()
                allSignalements.addAll(result)
                applyFiltersAndSort()
            } catch (e: Exception) {
                println("Erreur : ${e.message}")
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
                commentaires.addAll(result.map { it.copy(signalementId = signalementId) })
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
                commentaires.add(0, commentaire.copy(signalementId = signalementId))
                println("✅ Commentaire ajouté")
            } catch (e: Exception) {
                println("❌ Erreur ajout commentaire: ${e.message}")
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

    private fun applyFiltersAndSort() {
        var list = allSignalements.toList()

        if (searchQuery.value.isNotBlank()) {
            list = list.filter {
                it.titre.contains(searchQuery.value, ignoreCase = true) ||
                        it.description?.contains(searchQuery.value, ignoreCase = true) == true
            }
        }

        if (sortByDate.value) {
            list = DateUtils.sortByDateDesc(list)
        }

        mesSignalements.clear()
        mesSignalements.addAll(list)
    }

    fun search(query: String) {
        searchQuery.value = query
        applyFiltersAndSort()
    }

    fun toggleSort() {
        sortByDate.value = !sortByDate.value
        applyFiltersAndSort()
    }

    // ✅ AJOUT : CHANGER LE MOT DE PASSE
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
}