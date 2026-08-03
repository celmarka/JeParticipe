package com.citoyen.jeparticipe.ui.citoyen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.ui.common.CommentairesDialog
import com.citoyen.jeparticipe.utils.DateUtils

@Composable
fun MesSignalementsScreen(
    viewModel: SignalementViewModel,
    onBack: () -> Unit
) {
    var selectedSignalement by remember { mutableStateOf<Signalement?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    var showCommentairesDialog by remember { mutableStateOf(false) }
    var selectedSignalementId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.chargerMesSignalements()
    }

    val total = viewModel.mesSignalements.size
    val enAttente = viewModel.mesSignalements.count { it.statut?.uppercase() == "EN_ATTENTE" }
    val enCours = viewModel.mesSignalements.count { it.statut?.uppercase() == "EN_COURS" }
    val resolus = viewModel.mesSignalements.count { it.statut?.uppercase() == "RESOLU" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
    ) {
        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF1A237E),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Mes Signalements",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            IconButton(
                onClick = { viewModel.toggleSort() },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (viewModel.sortByDate.value) Color(0xFF1A237E) else Color.White,
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Trier par date",
                    tint = if (viewModel.sortByDate.value) Color.White else Color(0xFF1A237E)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModel.search(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            placeholder = {
                Text(
                    text = "Recherche",
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Rechercher",
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchText = ""
                            viewModel.search("")
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Effacer",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1A237E),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Carte de statistiques
        if (total > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 Statistiques",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A237E)
                        )
                        Text(
                            text = "${total} signalement(s)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(total, "Total", Color(0xFF1A237E))
                        StatItem(enAttente, "En attente", Color(0xFFFFA726))
                        StatItem(enCours, "En cours", Color(0xFF42A5F5))
                        StatItem(resolus, "Résolus", Color(0xFF66BB6A))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Liste des signalements
        if (viewModel.mesSignalements.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE8EAF6)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchText.isNotEmpty()) "🔍" else "📭",
                                fontSize = 48.sp
                            )
                        }
                    }
                    Text(
                        text = if (searchText.isNotEmpty()) "Aucun résultat" else "Aucun signalement",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = if (searchText.isNotEmpty())
                            "Aucun signalement ne correspond à votre recherche"
                        else
                            "Créez votre premier signalement\net contribuez à l'amélioration de votre ville",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.mesSignalements) { signalement ->
                    val nonLus = viewModel.commentaires.filter {
                        it.signalementId == signalement.id && !it.lu
                    }.size

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        SignalementCard(
                            signalement = signalement,
                            onDetailsClick = {
                                selectedSignalement = signalement
                                showDetailsDialog = true
                            },
                            onCommentairesClick = {
                                selectedSignalementId = signalement.id
                                viewModel.chargerCommentaires(signalement.id!!)
                                showCommentairesDialog = true
                            },
                            commentairesNonLus = nonLus
                        )
                    }
                }
            }
        }
    }

    // Dialog des détails
    if (showDetailsDialog && selectedSignalement != null) {
        SignalementDetailsDialog(
            signalement = selectedSignalement!!,
            onDismiss = {
                showDetailsDialog = false
                selectedSignalement = null
            }
        )
    }

    // Dialog des commentaires
    if (showCommentairesDialog && selectedSignalementId != null) {
        CommentairesDialog(
            signalementId = selectedSignalementId!!,
            commentaires = viewModel.commentaires,
            isAgentOrAdmin = false,
            isLoading = viewModel.isLoadingCommentaires.value,
            onAjouterCommentaire = { contenu, estJustification ->
                viewModel.ajouterCommentaire(selectedSignalementId!!, contenu, estJustification)
            },
            onSupprimerCommentaire = { commentaireId ->
                viewModel.supprimerCommentaire(commentaireId)
            },
            onMarquerCommeLu = { commentaireId ->
                viewModel.marquerCommeLu(commentaireId)
            },
            onMarquerTousCommeLus = {
                viewModel.marquerTousCommeLus(selectedSignalementId!!)
            },
            onDismiss = {
                showCommentairesDialog = false
                selectedSignalementId = null
            }
        )
    }
}

@Composable
fun StatItem(count: Int, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SignalementCard(
    signalement: Signalement,
    onDetailsClick: () -> Unit,
    onCommentairesClick: () -> Unit,
    commentairesNonLus: Int = 0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        shape = RoundedCornerShape(16.dp),
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1A237E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                StatusChip(status = signalement.statut)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = signalement.description ?: "Aucune description",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            if (!signalement.dateCreation.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
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
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            if (!signalement.photo.isNullOrEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Photo",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF42A5F5)
                    )
                    Text(
                        text = "Photo disponible",
                        fontSize = 11.sp,
                        color = Color(0xFF42A5F5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8EAF6),
                        contentColor = Color(0xFF1A237E)
                    )
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Détails",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedButton(
                        onClick = onCommentairesClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF1A237E)
                        )
                    ) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = "Commentaires",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Commentaires",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    if (commentairesNonLus > 0) {
                        Badge(
                            containerColor = Color(0xFFEF5350),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                        ) {
                            Text(
                                text = if (commentairesNonLus > 99) "99+" else commentairesNonLus.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String?) {
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(50)
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

// ✅ FONCTION POUR DÉCODER LE BASE64 EN IMAGE BITMAP
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
fun SignalementDetailsDialog(
    signalement: Signalement,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // En-tête
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📄 Détails du signalement",
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

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(label = "Titre", value = signalement.titre)
                    HorizontalDivider()
                    DetailRow(label = "Description", value = signalement.description ?: "Aucune description")
                    HorizontalDivider()
                    DetailRow(label = "Catégorie", value = signalement.categorie ?: "Non spécifiée")
                    HorizontalDivider()
                    DetailRow(label = "Adresse", value = signalement.adresse ?: "Non spécifiée")
                    HorizontalDivider()
                    DetailRow(label = "Statut", value = signalement.statut ?: "Inconnu", isStatus = true)
                    HorizontalDivider()
                    if (!signalement.dateCreation.isNullOrEmpty()) {
                        DetailRow(label = "Date de création", value = DateUtils.formatDate(signalement.dateCreation))
                        HorizontalDivider()
                    }

                    // ✅ AFFICHAGE DE LA PHOTO
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
                            DetailRow(label = "Photo", value = "❌ Format non supporté")
                        }
                    } else {
                        DetailRow(label = "Photo", value = "❌ Non disponible")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
fun DetailRow(label: String, value: String, isStatus: Boolean = false) {
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