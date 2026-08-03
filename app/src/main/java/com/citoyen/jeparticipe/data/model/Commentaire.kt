package com.citoyen.jeparticipe.data.model

import com.google.gson.annotations.SerializedName

data class Commentaire(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("contenu")
    val contenu: String,

    @SerializedName("nomUtilisateur")
    val nomUtilisateur: String? = null,

    @SerializedName("prenomUtilisateur")
    val prenomUtilisateur: String? = null,

    @SerializedName("roleUtilisateur")
    val roleUtilisateur: String? = null,

    @SerializedName("dateCreation")
    val dateCreation: String? = null,

    @SerializedName("estJustification")
    val estJustification: Boolean = false,

    @SerializedName("lu")
    val lu: Boolean = false,

    // ✅ Ce champ est uniquement pour le frontend, il n'est pas envoyé au backend
    val signalementId: Long? = null
)