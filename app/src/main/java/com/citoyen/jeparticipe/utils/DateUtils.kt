package com.citoyen.jeparticipe.utils

import com.citoyen.jeparticipe.data.model.Signalement
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.sortedByDescending

object DateUtils {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault())

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Date inconnue"
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    // ✅ Fonction pour parser une date
    fun parseDate(dateString: String?): Date? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            inputFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    // ✅ Fonction pour trier les signalements par date (plus récent en premier)
    fun sortByDateDesc(signalements: List<Signalement>): List<Signalement> {
        return signalements.sortedByDescending {
            parseDate(it.dateCreation) ?: Date(0)
        }
    }
}