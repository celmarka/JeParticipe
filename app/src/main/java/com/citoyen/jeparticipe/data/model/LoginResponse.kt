package com.citoyen.jeparticipe.data.model


data class LoginResponse(

    val token: String,

    val id: Long,

    val nom: String,

    val prenom: String,

    val email: String,

    val role: String

)