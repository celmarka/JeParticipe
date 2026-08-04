package com.citoyen.jeparticipe.data.repository

import com.citoyen.jeparticipe.data.api.RetrofitClient
import com.citoyen.jeparticipe.data.local.SessionManager
import com.citoyen.jeparticipe.data.model.ChangePasswordRequest
import com.citoyen.jeparticipe.data.model.LoginRequest
import com.citoyen.jeparticipe.data.model.LoginResponse
import com.citoyen.jeparticipe.data.model.RegisterRequest
import com.citoyen.jeparticipe.data.model.RegisterResponse
import retrofit2.HttpException

class AuthRepository(
    private val sessionManager: SessionManager
) {
    private val apiService = RetrofitClient.apiService

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            sessionManager.saveToken(response.token)
            sessionManager.saveUserRole(response.role)
            sessionManager.saveUserId(response.id)
            sessionManager.saveUserNom(response.nom)
            sessionManager.saveUserPrenom(response.prenom)
            sessionManager.saveUserEmail(response.email)
            response
        } catch (e: Exception) {
            throw Exception("Erreur de connexion: ${e.message}")
        }
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return try {
            apiService.register(request)
        } catch (e: Exception) {
            throw Exception("Erreur d'inscription: ${e.message}")
        }
    }

    suspend fun enregistrerTokenFCM(token: String): Boolean {
        return try {
            println("📤 Envoi du token FCM à l'API: $token")
            val response = apiService.updateFcmToken(mapOf("token" to token))
            println("✅ Réponse du backend: ${response.email}")
            sessionManager.saveFirebaseToken(token)
            true
        } catch (e: Exception) {
            println("❌ Erreur enregistrement token FCM: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        return try {
            val api = RetrofitClient.getAuthenticatedApi(sessionManager)
            val response = api.changePassword(
                ChangePasswordRequest(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
            )
            println("✅ Mot de passe changé avec succès pour: ${response.email}")
            true
        } catch (e: HttpException) {
            val errorCode = e.code()
            val errorBody = e.response()?.errorBody()?.string()
            println("❌ Erreur HTTP $errorCode: $errorBody")
            false
        } catch (e: Exception) {
            println("❌ Erreur: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // ✅ Récupérer l'ID de l'utilisateur connecté via SessionManager
    fun getCurrentUserId(): Long? {
        return sessionManager.getUserId()
    }
}