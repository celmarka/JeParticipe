
package com.citoyen.jeparticipe.data.model

import com.google.gson.annotations.SerializedName

data class Signalement(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("titre")
    val titre: String = "",

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("adresse")
    val adresse: String? = null,

    @SerializedName("photo")
    val photo: String? = null,  // Peut être URL ou Base64

    @SerializedName("statut")
    val statut: String? = null,

    @SerializedName("categorie")
    val categorie: String? = null,

    @SerializedName("dateCreation")
    val dateCreation: String? = null,

    @SerializedName("citoyen")  // ✅ AJOUTER CE CHAMP
    val citoyen: User? = null
)
