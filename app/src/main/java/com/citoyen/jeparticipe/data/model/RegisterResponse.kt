package com.citoyen.jeparticipe.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nom")
    val nom: String? = null,

    @SerializedName("prenom")
    val prenom: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("telephone")
    val telephone: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("actif")
    val actif: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("success")
    val success: Boolean = true
)