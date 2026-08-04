package com.citoyen.jeparticipe.ui.agent

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.citoyen.jeparticipe.utils.DateUtils

// Fonction utilitaire pour décoder le Base64 en ImageBitmap
fun decodeBase64ToImageBitmap(base64String: String?): ImageBitmap? {
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
fun AgentSignalementCard(
    signalement: Signalement,
    onUpdateStatut: (String) -> Unit,
    onCommentairesClick: () -> Unit
) {
    var showDetailsDialog by remember { mutableStateOf(false) }

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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Category,
                            contentDescription = "Catégorie",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = signalement.categorie ?: "Non spécifiée",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Adresse",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = signalement.adresse ?: "",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // AUTEUR (citoyen)
                val citoyen = signalement.citoyen
                if (citoyen != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Auteur",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF1A237E)
                        )
                        Text(
                            text = "${citoyen.prenom} ${citoyen.nom}",
                            fontSize = 12.sp,
                            color = Color(0xFF1A237E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // ✅ AGENT ASSIGNÉ (si présent)
                val agent = signalement.agent
                if (agent != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = "Agent assigné",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF42A5F5)
                        )
                        Text(
                            text = "Assigné à : ${agent.prenom} ${agent.nom} (${agent.email})",
                            fontSize = 12.sp,
                            color = Color(0xFF42A5F5),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Optionnel : indiquer que le signalement n'est pas encore assigné
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = "Non assigné",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFA726)
                        )
                        Text(
                            text = "Non assigné",
                            fontSize = 12.sp,
                            color = Color(0xFFFFA726),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Date
                if (!signalement.dateCreation.isNullOrEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Date",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = DateUtils.formatDate(signalement.dateCreation),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Boutons actions selon statut
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
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prendre", fontSize = 12.sp, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = { onUpdateStatut("REJETE") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF5350)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFEF5350)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rejeter", fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = { showDetailsDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Détails",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF1A237E)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Voir détails", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onCommentairesClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1A237E)
                            )
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "Commentaires",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Commentaires", fontSize = 12.sp)
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
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Résoudre", fontSize = 12.sp, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = { onUpdateStatut("REJETE") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF5350)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFEF5350)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rejeter", fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = { showDetailsDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Détails",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF1A237E)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Voir détails", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onCommentairesClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1A237E)
                            )
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "Commentaires",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Commentaires", fontSize = 12.sp)
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (signalement.statut?.uppercase() == "RESOLU")
                                        Icons.Default.CheckCircle
                                    else
                                        Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (signalement.statut?.uppercase() == "RESOLU")
                                        Color(0xFF66BB6A)
                                    else
                                        Color(0xFFEF5350)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
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
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showDetailsDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Détails",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF1A237E)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Voir détails", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onCommentairesClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1A237E)
                            )
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "Commentaires",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Commentaires", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Dialog des détails
    if (showDetailsDialog) {
        AgentSignalementDetailsDialog(
            signalement = signalement,
            onDismiss = { showDetailsDialog = false }
        )
    }
}

// ✅ Dialog des détails POUR AGENT avec affichage de l'agent
@Composable
fun AgentSignalementDetailsDialog(
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
                        text = "Détails",
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
                    DetailRowAgent(label = "Titre", value = signalement.titre)
                    HorizontalDivider()

                    DetailRowAgent(label = "Description", value = signalement.description ?: "Aucune description")
                    HorizontalDivider()

                    DetailRowAgent(label = "Catégorie", value = signalement.categorie ?: "Non spécifiée")
                    HorizontalDivider()

                    DetailRowAgent(label = "Adresse", value = signalement.adresse ?: "Non spécifiée")
                    HorizontalDivider()

                    val citoyen = signalement.citoyen
                    DetailRowAgent(
                        label = "Auteur",
                        value = if (citoyen != null) "${citoyen.prenom} ${citoyen.nom}" else "Citoyen inconnu"
                    )
                    HorizontalDivider()

                    // ✅ Affichage de l'agent assigné dans les détails
                    val agent = signalement.agent
                    DetailRowAgent(
                        label = "Agent assigné",
                        value = if (agent != null) "${agent.prenom} ${agent.nom} (${agent.email})" else "Aucun agent assigné"
                    )
                    HorizontalDivider()

                    DetailRowAgent(
                        label = "Latitude",
                        value = signalement.latitude?.toString() ?: "Non spécifiée"
                    )
                    DetailRowAgent(
                        label = "Longitude",
                        value = signalement.longitude?.toString() ?: "Non spécifiée"
                    )
                    HorizontalDivider()

                    DetailRowAgent(
                        label = "Statut",
                        value = signalement.statut ?: "Inconnu",
                        isStatus = true
                    )
                    HorizontalDivider()

                    if (!signalement.dateCreation.isNullOrEmpty()) {
                        DetailRowAgent(
                            label = "Date de création",
                            value = DateUtils.formatDate(signalement.dateCreation)
                        )
                        HorizontalDivider()
                    }

                    // Photo
                    if (!signalement.photo.isNullOrEmpty()) {
                        val imageBitmap = decodeBase64ToImageBitmap(signalement.photo)
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
                            DetailRowAgent(label = "Photo", value = "❌ Format non supporté")
                        }
                    } else {
                        DetailRowAgent(label = "Photo", value = "❌ Non disponible")
                    }
                    HorizontalDivider()

                    DetailRowAgent(
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
fun DetailRowAgent(label: String, value: String, isStatus: Boolean = false) {
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

@Composable
fun StatusBadge(status: String?) {
    val (color, label) = when (status?.uppercase()) {
        "EN_ATTENTE" -> Color(0xFFFFA726) to "En attente"
        "EN_COURS" -> Color(0xFF42A5F5) to "En cours"
        "RESOLU" -> Color(0xFF66BB6A) to "Résolu"
        "REJETE" -> Color(0xFFEF5350) to "Rejeté"
        else -> Color.Gray to "Inconnu"
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(6.dp)
            ) {
                if (status?.uppercase() == "EN_COURS") {
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.5.dp,
                        color = color
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(color, RoundedCornerShape(50))
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}