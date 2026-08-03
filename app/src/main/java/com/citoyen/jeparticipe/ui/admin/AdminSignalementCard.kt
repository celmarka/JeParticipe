package com.citoyen.jeparticipe.ui.admin

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.ui.agent.StatusBadge
import com.citoyen.jeparticipe.utils.DateUtils

// ✅ Fonction utilitaire (copiée ici pour l’autonomie)
fun decodeBase64ToImageBitmapAdmin(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrEmpty()) return null
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AdminSignalementCard(
    signalement: Signalement,
    onUpdateStatut: (String) -> Unit,
    onCommentairesClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Titre + Statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = signalement.titre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A237E),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                StatusBadge(status = signalement.statut)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = signalement.description ?: "Aucune description",
                fontSize = 13.sp,
                color = Color(0xFF424242),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Informations
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📂 ${signalement.categorie ?: "Non spécifiée"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "📍 ${signalement.adresse ?: ""}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val citoyen = signalement.citoyen
                if (citoyen != null) {
                    Text(
                        text = "👤 ${citoyen.prenom} ${citoyen.nom}",
                        fontSize = 12.sp,
                        color = Color(0xFF1A237E),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!signalement.dateCreation.isNullOrEmpty()) {
                    Text(
                        text = "📅 ${DateUtils.formatDate(signalement.dateCreation)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions (inchangé)
            when (signalement.statut?.uppercase()) {
                "EN_ATTENTE" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onUpdateStatut("EN_COURS") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF42A5F5)
                                )
                            ) {
                                Text("Prendre", fontSize = 12.sp, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = { onUpdateStatut("REJETE") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Rejeter", fontSize = 12.sp)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDetailsDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Détails",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF1A237E)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Plus", fontSize = 9.sp)
                            }
                            OutlinedButton(
                                onClick = onCommentairesClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("💬", fontSize = 16.sp)
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF5350)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFEF5350)
                                )
                            }
                        }
                    }
                }
                "EN_COURS" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onUpdateStatut("RESOLU") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF66BB6A)
                                )
                            ) {
                                Text("Résoudre", fontSize = 12.sp, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = { onUpdateStatut("REJETE") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Rejeter", fontSize = 12.sp)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDetailsDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Détails",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF1A237E)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Détails", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onCommentairesClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("💬", fontSize = 16.sp)
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF5350)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFEF5350)
                                )
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = if (signalement.statut?.uppercase() == "RESOLU")
                                Color(0xFFE8F5E9)
                            else
                                Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = if (signalement.statut?.uppercase() == "RESOLU")
                                    "✅ Signalement résolu"
                                else
                                    "❌ Signalement rejeté",
                                fontSize = 13.sp,
                                color = if (signalement.statut?.uppercase() == "RESOLU")
                                    Color(0xFF2E7D32)
                                else
                                    Color(0xFFC62828),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDetailsDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Détails",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF1A237E)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Détails", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onCommentairesClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("💬", fontSize = 16.sp)
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF5350)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFEF5350)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ✅ Dialog de confirmation suppression
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Supprimer le signalement",
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Êtes-vous sûr de vouloir supprimer ce signalement ?")
                    Text(
                        text = "\"${signalement.titre}\"",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = "Cette action est irréversible.",
                        color = Color(0xFFEF5350),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler", color = Color(0xFF1A237E))
                }
            }
        )
    }

    // ✅ Dialog des détails ADMIN avec photo
    if (showDetailsDialog) {
        AdminSignalementDetailsDialog(
            signalement = signalement,
            onDismiss = { showDetailsDialog = false }
        )
    }
}

// ✅ Dialog des détails ADMIN (avec affichage de la photo)
@Composable
fun AdminSignalementDetailsDialog(
    signalement: Signalement,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Plus",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRowAdmin(label = "Titre", value = signalement.titre)
                    HorizontalDivider()

                    DetailRowAdmin(label = "Description", value = signalement.description ?: "Aucune description")
                    HorizontalDivider()

                    DetailRowAdmin(label = "Catégorie", value = signalement.categorie ?: "Non spécifiée")
                    HorizontalDivider()

                    DetailRowAdmin(label = "Adresse", value = signalement.adresse ?: "Non spécifiée")
                    HorizontalDivider()

                    val citoyen = signalement.citoyen
                    DetailRowAdmin(
                        label = "Auteur",
                        value = if (citoyen != null) "${citoyen.prenom} ${citoyen.nom}" else "Citoyen inconnu"
                    )
                    HorizontalDivider()

                    DetailRowAdmin(
                        label = "Latitude",
                        value = signalement.latitude?.toString() ?: "Non spécifiée"
                    )
                    DetailRowAdmin(
                        label = "Longitude",
                        value = signalement.longitude?.toString() ?: "Non spécifiée"
                    )
                    HorizontalDivider()

                    DetailRowAdmin(
                        label = "Statut",
                        value = signalement.statut ?: "Inconnu",
                        isStatus = true
                    )
                    HorizontalDivider()

                    if (!signalement.dateCreation.isNullOrEmpty()) {
                        DetailRowAdmin(
                            label = "Date de création",
                            value = DateUtils.formatDate(signalement.dateCreation)
                        )
                        HorizontalDivider()
                    }

                    // ✅ AFFICHAGE DE LA PHOTO
                    if (!signalement.photo.isNullOrEmpty()) {
                        val imageBitmap = decodeBase64ToImageBitmapAdmin(signalement.photo)
                        if (imageBitmap != null) {
                            Column {
                                Text(
                                    text = "📷 Photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF757575)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Photo du signalement",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            HorizontalDivider()
                        } else {
                            DetailRowAdmin(label = "Photo", value = "❌ Format non supporté")
                        }
                    } else {
                        DetailRowAdmin(label = "Photo", value = "❌ Non disponible")
                    }
                    HorizontalDivider()

                    DetailRowAdmin(
                        label = "ID",
                        value = signalement.id?.toString() ?: "N/A"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A237E)
                    )
                ) {
                    Text("Fermer", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun DetailRowAdmin(label: String, value: String, isStatus: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isStatus) {
                when (value.uppercase()) {
                    "EN_ATTENTE" -> Color(0xFFFFA726)
                    "EN_COURS" -> Color(0xFF42A5F5)
                    "RESOLU" -> Color(0xFF66BB6A)
                    "REJETE" -> Color(0xFFEF5350)
                    else -> Color(0xFF1A237E)
                }
            } else {
                Color(0xFF1A237E)
            }
        )
    }
}