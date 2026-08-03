package com.citoyen.jeparticipe.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nom")
    val nom: String,

    @SerializedName("prenom")
    val prenom: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("telephone")
    val telephone: String? = null,

    @SerializedName("password")
    val password: String
)