package com.citoyen.jeparticipe.data.api

import com.citoyen.jeparticipe.data.model.*
import retrofit2.http.*

interface ApiService {

    // ============ AUTHENTIFICATION ============
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    // ============ CITOYEN ============
    @GET("api/signalements/mes-signalements")
    suspend fun getMesSignalements(): List<Signalement>

    @POST("api/signalements")
    suspend fun createSignalement(
        @Body signalement: Signalement
    ): Signalement

    // ============ AGENT / ADMIN ============
    @GET("api/signalements")
    suspend fun getAllSignalements(): List<Signalement>

    @GET("api/signalements/{id}")
    suspend fun getSignalementById(@Path("id") id: Long): Signalement

    @GET("api/signalements/statut")
    suspend fun getSignalementsByStatut(@Query("statut") statut: String): List<Signalement>

    @PUT("api/signalements/{id}/statut")
    suspend fun updateStatut(
        @Path("id") id: Long,
        @Body statut: Map<String, String>
    ): Signalement

    // ============ ADMIN ============
    @GET("api/users")
    suspend fun getAllUsers(): List<User>

    @PUT("api/users/{id}/role")
    suspend fun updateUserRole(
        @Path("id") id: Long,
        @Body role: Map<String, String>
    ): User

    @PUT("api/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") id: Long
    ): User

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Path("id") id: Long
    ): Unit

    // ✅ AJOUT : Supprimer un signalement (Admin uniquement)
    @DELETE("api/signalements/{id}")
    suspend fun deleteSignalement(
        @Path("id") id: Long
    ): Unit

    // ✅ AJOUT : Changer le mot de passe
    @PUT("api/users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): User

    // ============ COMMENTAIRES ============
    @POST("api/commentaires/signalement/{signalementId}")
    suspend fun ajouterCommentaire(
        @Path("signalementId") signalementId: Long,
        @Body commentaire: Commentaire
    ): Commentaire

    @GET("api/commentaires/signalement/{signalementId}")
    suspend fun getCommentaires(
        @Path("signalementId") signalementId: Long
    ): List<Commentaire>

    @GET("api/commentaires/signalement/{signalementId}/justifications")
    suspend fun getJustifications(
        @Path("signalementId") signalementId: Long
    ): List<Commentaire>

    @DELETE("api/commentaires/{commentaireId}")
    suspend fun supprimerCommentaire(
        @Path("commentaireId") commentaireId: Long
    ): Unit

    // ============ COMMENTAIRES NON LUS ============
    @PUT("api/commentaires/{commentaireId}/lu")
    suspend fun marquerCommeLu(
        @Path("commentaireId") commentaireId: Long
    ): Unit

    @PUT("api/commentaires/signalement/{signalementId}/lu/tous")
    suspend fun marquerTousCommeLus(
        @Path("signalementId") signalementId: Long
    ): Unit

    @GET("api/commentaires/signalement/{signalementId}/non-lus")
    suspend fun getCommentairesNonLus(
        @Path("signalementId") signalementId: Long
    ): List<Commentaire>

    @GET("api/commentaires/signalement/{signalementId}/non-lus/count")
    suspend fun countCommentairesNonLus(
        @Path("signalementId") signalementId: Long
    ): Long
    // Assignation
    @PUT("api/signalements/{id}/assigner")
    suspend fun assignerAgent(
        @Path("id") id: Long,
        @Body request: Map<String, Long>
    ): Signalement

    @GET("api/users/agents")
    suspend fun getAgents(): List<User>

    // ✅ AJOUT : Enregistrer le token FCM
    @PUT("api/users/fcm-token")
    suspend fun updateFcmToken(
        @Body token: Map<String, String>
    ): User
}