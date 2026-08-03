package com.citoyen.jeparticipe.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.citoyen.jeparticipe.data.model.Commentaire
import com.citoyen.jeparticipe.utils.DateUtils

@Composable
fun CommentairesDialog(
    signalementId: Long,
    commentaires: List<Commentaire>,
    isAgentOrAdmin: Boolean,
    isLoading: Boolean,
    onAjouterCommentaire: (String, Boolean) -> Unit,
    onSupprimerCommentaire: (Long) -> Unit,
    onMarquerCommeLu: (Long) -> Unit,
    onMarquerTousCommeLus: () -> Unit,
    onDismiss: () -> Unit
) {
    var nouveauCommentaire by remember { mutableStateOf("") }
    var estJustification by remember { mutableStateOf(false) }

    val nonLus = commentaires.count { !it.lu }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // En-tête avec badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💬 Commentaires",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )
                        if (nonLus > 0) {
                            Badge(
                                containerColor = Color(0xFFEF5350)
                            ) {
                                Text(
                                    text = nonLus.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (nonLus > 0) {
                            TextButton(
                                onClick = onMarquerTousCommeLus,
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "Tout lire",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1A237E),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Text("✕", fontSize = 20.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Liste des commentaires
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                    }
                } else if (commentaires.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun commentaire",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(commentaires) { commentaire ->
                            CommentaireItem(
                                commentaire = commentaire,
                                onSupprimer = {
                                    onSupprimerCommentaire(commentaire.id!!)
                                },
                                onMarquerCommeLu = {
                                    if (!commentaire.lu) {
                                        onMarquerCommeLu(commentaire.id!!)
                                    }
                                },
                                isAgentOrAdmin = isAgentOrAdmin
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Champ de saisie
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nouveauCommentaire,
                        onValueChange = { nouveauCommentaire = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Écrire un commentaire...") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isAgentOrAdmin) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = estJustification,
                                    onCheckedChange = { estJustification = it }
                                )
                                Text(
                                    text = "Justification de rejet",
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF5350)
                                )
                            }
                            if (estJustification) {
                                Text(
                                    text = "⚠️ Visible par le citoyen",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFFA726)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (nouveauCommentaire.isNotBlank()) {
                                onAjouterCommentaire(nouveauCommentaire, estJustification)
                                nouveauCommentaire = ""
                                estJustification = false
                            }
                        },
                        enabled = nouveauCommentaire.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Envoyer", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentaireItem(
    commentaire: Commentaire,
    onSupprimer: () -> Unit,
    onMarquerCommeLu: () -> Unit,
    isAgentOrAdmin: Boolean
) {
    val isNonLu = !commentaire.lu

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isNonLu) {
                    onMarquerCommeLu()
                }
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                commentaire.estJustification -> Color(0xFFFFEBEE)
                isNonLu -> Color(0xFFE3F2FD)
                else -> Color(0xFFF5F5F5)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${commentaire.prenomUtilisateur} ${commentaire.nomUtilisateur}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1A237E)
                    )
                    if (commentaire.estJustification) {
                        Surface(
                            color = Color(0xFFEF5350).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "⚖️ Justification",
                                fontSize = 9.sp,
                                color = Color(0xFFEF5350),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (isNonLu) {
                        Surface(
                            color = Color(0xFFEF5350),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Nouveau",
                                fontSize = 8.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = commentaire.dateCreation?.let { DateUtils.formatDate(it) } ?: "",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    if (isAgentOrAdmin || commentaire.roleUtilisateur == "ADMIN") {
                        IconButton(
                            onClick = onSupprimer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("🗑️", fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = commentaire.contenu,
                fontSize = 13.sp,
                color = Color(0xFF424242)
            )
        }
    }
}