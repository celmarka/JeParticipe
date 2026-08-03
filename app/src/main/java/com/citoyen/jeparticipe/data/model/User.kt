package com.citoyen.jeparticipe.data.model

data class User(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String?,
    val role: String,
    val actif: Boolean
)