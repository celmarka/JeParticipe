package com.citoyen.jeparticipe.data.repository

import android.util.Log
import com.citoyen.jeparticipe.data.api.RetrofitClient
import com.citoyen.jeparticipe.data.local.SessionManager
import com.citoyen.jeparticipe.data.model.ChangePasswordRequest
import com.citoyen.jeparticipe.data.model.Commentaire
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.data.model.User
import retrofit2.HttpException

class SignalementRepository(
    private val sessionManager: SessionManager
) {
    private val apiService
        get() = RetrofitClient.getAuthenticatedApi(sessionManager)

    // ============ CITOYEN ============
    suspend fun getMesSignalements(): List<Signalement> {
        return try {
            apiService.getMesSignalements()
        } catch (e: Exception) {
            throw Exception("Impossible de charger vos signalements: ${e.message}")
        }
    }

    suspend fun createSignalement(
        titre: String,
        description: String,
        latitude: Double?,
        longitude: Double?,
        adresse: String,
        categorie: String,
        photoBase64: String? = null
    ): Signalement {
        return try {
            val signalement = Signalement(
                titre = titre,
                description = description,
                latitude = latitude,
                longitude = longitude,
                adresse = adresse,
                categorie = categorie,
                photo = photoBase64,
                statut = "EN_ATTENTE"
            )
            apiService.createSignalement(signalement)
        } catch (e: Exception) {
            Log.e("SignalementRepo", "Erreur création: ${e.message}")
            throw Exception("Impossible de créer le signalement: ${e.message}")
        }
    }

    // ============ AGENT / ADMIN ============
    suspend fun getAllSignalements(): List<Signalement> {
        return try {
            apiService.getAllSignalements()
        } catch (e: Exception) {
            throw Exception("Impossible de charger les signalements: ${e.message}")
        }
    }

    suspend fun updateStatut(id: Long, statut: String): Signalement {
        return try {
            apiService.updateStatut(id, mapOf("statut" to statut))
        } catch (e: Exception) {
            throw Exception("Impossible de mettre à jour le statut: ${e.message}")
        }
    }

    // ============ ADMIN ============
    suspend fun getAllUsers(): List<User> {
        return try {
            apiService.getAllUsers()
        } catch (e: Exception) {
            throw Exception("Impossible de charger les utilisateurs: ${e.message}")
        }
    }

    suspend fun updateUserRole(id: Long, role: String): User {
        return try {
            apiService.updateUserRole(id, mapOf("role" to role))
        } catch (e: Exception) {
            throw Exception("Impossible de mettre à jour le rôle: ${e.message}")
        }
    }

    suspend fun updateUserStatus(id: Long): User {
        return try {
            apiService.updateUserStatus(id)
        } catch (e: Exception) {
            throw Exception("Impossible de changer le statut: ${e.message}")
        }
    }

    suspend fun deleteUser(id: Long) {
        return try {
            apiService.deleteUser(id)
        } catch (e: Exception) {
            throw Exception("Impossible de supprimer l'utilisateur: ${e.message}")
        }
    }

    // ============ COMMENTAIRES ============
    suspend fun ajouterCommentaire(signalementId: Long, contenu: String, estJustification: Boolean = false): Commentaire {
        return try {
            val commentaire = Commentaire(
                contenu = contenu,
                estJustification = estJustification
            )
            apiService.ajouterCommentaire(signalementId, commentaire)
        } catch (e: Exception) {
            throw Exception("Impossible d'ajouter le commentaire: ${e.message}")
        }
    }

    suspend fun getCommentaires(signalementId: Long): List<Commentaire> {
        return try {
            apiService.getCommentaires(signalementId)
        } catch (e: Exception) {
            throw Exception("Impossible de charger les commentaires: ${e.message}")
        }
    }

    suspend fun getJustifications(signalementId: Long): List<Commentaire> {
        return try {
            apiService.getJustifications(signalementId)
        } catch (e: Exception) {
            throw Exception("Impossible de charger les justifications: ${e.message}")
        }
    }

    suspend fun supprimerCommentaire(commentaireId: Long) {
        return try {
            apiService.supprimerCommentaire(commentaireId)
        } catch (e: Exception) {
            throw Exception("Impossible de supprimer le commentaire: ${e.message}")
        }
    }

    // ============ COMMENTAIRES NON LUS ============
    suspend fun marquerCommeLu(commentaireId: Long) {
        return try {
            apiService.marquerCommeLu(commentaireId)
        } catch (e: Exception) {
            throw Exception("Impossible de marquer comme lu: ${e.message}")
        }
    }

    suspend fun marquerTousCommeLus(signalementId: Long) {
        return try {
            apiService.marquerTousCommeLus(signalementId)
        } catch (e: Exception) {
            throw Exception("Impossible de marquer tous comme lus: ${e.message}")
        }
    }

    suspend fun getCommentairesNonLus(signalementId: Long): List<Commentaire> {
        return try {
            apiService.getCommentairesNonLus(signalementId)
        } catch (e: Exception) {
            throw Exception("Impossible de charger les commentaires non lus: ${e.message}")
        }
    }

    suspend fun countCommentairesNonLus(signalementId: Long): Long {
        return try {
            apiService.countCommentairesNonLus(signalementId)
        } catch (e: Exception) {
            throw Exception("Impossible de compter les commentaires non lus: ${e.message}")
        }
    }

    // Supprimer un signalement
    suspend fun deleteSignalement(id: Long) {
        return try {
            apiService.deleteSignalement(id)
        } catch (e: Exception) {
            throw Exception("Impossible de supprimer le signalement: ${e.message}")
        }
    }
    //Assignation de signalement
    suspend fun assignerAgent(signalementId: Long, agentId: Long): Signalement {
        return try {
            apiService.assignerAgent(signalementId, mapOf("agentId" to agentId))
        } catch (e: Exception) {
            throw Exception("Impossible d'assigner l'agent: ${e.message}")
        }
    }

    suspend fun getAgents(): List<User> {
        return try {
            apiService.getAgents()
        } catch (e: Exception) {
            throw Exception("Impossible de charger les agents: ${e.message}")
        }
    }

    //  Changer le mot de passe
    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        return try {
            val response = apiService.changePassword(
                ChangePasswordRequest(
                    oldPassword,
                    newPassword
                )
            )
            // Si on arrive ici, c'est que la requête a réussi
            println(" Mot de passe changé avec succès pour: ${response.email}")
            true
        } catch (e: HttpException) {
            // Erreur HTTP (401, 400, 500, etc.)
            val errorBody = e.response()?.errorBody()?.string()
            println("❌ Erreur HTTP changement mot de passe: ${e.code()} - $errorBody")
            false
        } catch (e: Exception) {
            // Autres erreurs
            println("❌ Erreur changement mot de passe: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}