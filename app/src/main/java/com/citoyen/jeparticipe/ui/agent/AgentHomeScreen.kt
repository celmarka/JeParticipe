package com.citoyen.jeparticipe.ui.agent

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.ui.common.CommentairesDialog
import com.citoyen.jeparticipe.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AgentHomeScreen(
    viewModel: AgentSignalementViewModel = viewModel(),
    onLogout: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showCommentairesDialog by remember { mutableStateOf(false) }
    var selectedSignalementId by remember { mutableStateOf<Long?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) } // ✅ Profil

    LaunchedEffect(Unit) {
        viewModel.chargerSignalements()
    }

    val signalements = viewModel.signalements
    val isLoading = viewModel.isLoading.value

    val total = signalements.size
    val enAttente = signalements.count { it.statut == "EN_ATTENTE" }
    val enCours = signalements.count { it.statut == "EN_COURS" }
    val resolus = signalements.count { it.statut == "RESOLU" }

    val filteredSignalements = when (selectedFilter) {
        "EN_ATTENTE" -> signalements.filter { it.statut == "EN_ATTENTE" }
        "EN_COURS" -> signalements.filter { it.statut == "EN_COURS" }
        "RESOLU" -> signalements.filter { it.statut == "RESOLU" }
        else -> signalements
    }

    val finalSignalements = if (searchText.isNotBlank()) {
        filteredSignalements.filter {
            it.titre.contains(searchText, ignoreCase = true) ||
                    it.description?.contains(searchText, ignoreCase = true) == true
        }
    } else {
        filteredSignalements
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // En-tête avec bouton Profil
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Agent",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ BOUTON PROFIL
                IconButton(
                    onClick = { showProfileDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF1A237E).copy(alpha = 0.1f),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = Color(0xFF1A237E)
                    )
                }

                Button(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFEF5350)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Déconnexion",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Déconnexion",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Dialog de confirmation déconnexion
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Déconnexion") },
                text = { Text("Êtes-vous sûr de vouloir vous déconnecter ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        )
                    ) {
                        Text("Se déconnecter", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Barre de recherche
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.search(it)
                },
                modifier = Modifier
                    .weight(1f)
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

            IconButton(
                onClick = { viewModel.toggleSort() },
                modifier = Modifier
                    .size(58.dp)
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

        // Statistiques cliquables
        if (total > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItemAgentClickable(
                        count = total,
                        label = "Total",
                        color = Color(0xFF1A237E),
                        isSelected = selectedFilter == null,
                        onClick = { selectedFilter = null }
                    )
                    StatItemAgentClickable(
                        count = enAttente,
                        label = "En attente",
                        color = Color(0xFFFFA726),
                        isSelected = selectedFilter == "EN_ATTENTE",
                        onClick = {
                            selectedFilter = if (selectedFilter == "EN_ATTENTE") null else "EN_ATTENTE"
                        }
                    )
                    StatItemAgentClickable(
                        count = enCours,
                        label = "En cours",
                        color = Color(0xFF42A5F5),
                        isSelected = selectedFilter == "EN_COURS",
                        onClick = {
                            selectedFilter = if (selectedFilter == "EN_COURS") null else "EN_COURS"
                        }
                    )
                    StatItemAgentClickable(
                        count = resolus,
                        label = "Résolus",
                        color = Color(0xFF66BB6A),
                        isSelected = selectedFilter == "RESOLU",
                        onClick = {
                            selectedFilter = if (selectedFilter == "RESOLU") null else "RESOLU"
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Titre de la liste
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 Signalements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A237E),
                fontSize = 14.sp
            )
            Text(
                text = "${finalSignalements.size} signalement(s)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Liste
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            }
            finalSignalements.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchText.isNotEmpty()) "Aucun résultat" else "Aucun signalement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(finalSignalements) { signalement ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            AgentSignalementCard(
                                signalement = signalement,
                                onUpdateStatut = { nouveauStatut ->
                                    viewModel.updateStatut(signalement, nouveauStatut)
                                },
                                onCommentairesClick = {
                                    selectedSignalementId = signalement.id
                                    viewModel.chargerCommentaires(signalement.id!!)
                                    showCommentairesDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog des commentaires
    if (showCommentairesDialog && selectedSignalementId != null) {
        CommentairesDialog(
            signalementId = selectedSignalementId!!,
            commentaires = viewModel.commentaires,
            isAgentOrAdmin = true,
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

    // ✅ Dialog PROFIL - changement de mot de passe
    if (showProfileDialog) {
        AgentChangePasswordDialog(
            viewModel = viewModel,
            onDismiss = { showProfileDialog = false }
        )
    }
}

// ---------------------------
// Composants de statistiques
// ---------------------------
@Composable
fun StatItemAgentClickable(
    count: Int,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = count.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) color else color.copy(alpha = 0.5f)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (isSelected) color else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

// ✅ Dialog pour changer le mot de passe (Agent)
@Composable
fun AgentChangePasswordDialog(
    viewModel: AgentSignalementViewModel,
    onDismiss: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        oldPassword = ""
        newPassword = ""
        confirmPassword = ""
        errorMessage = null
        showSuccess = false
        isLoading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF1A237E)
                )
                Text(
                    text = "Changer le mot de passe",
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Ancien mot de passe *") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nouveau mot de passe *") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmer *") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFEF5350),
                        fontSize = 13.sp
                    )
                }

                if (showSuccess) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✅ Mot de passe changé avec succès !",
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "Le mot de passe doit contenir au moins 6 caractères",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        oldPassword.isBlank() -> errorMessage = "Veuillez entrer votre ancien mot de passe"
                        newPassword.isBlank() -> errorMessage = "Veuillez entrer un nouveau mot de passe"
                        newPassword.length < 6 -> errorMessage = "Le mot de passe doit contenir au moins 6 caractères"
                        newPassword != confirmPassword -> errorMessage = "Les mots de passe ne correspondent pas"
                        else -> {
                            isLoading = true
                            viewModel.changePassword(
                                oldPassword = oldPassword,
                                newPassword = newPassword,
                                onSuccess = {
                                    isLoading = false
                                    showSuccess = true
                                    errorMessage = null
                                    coroutineScope.launch {
                                        delay(2000)
                                        onDismiss()
                                    }
                                },
                                onError = { error: String ->
                                    isLoading = false
                                    errorMessage = error
                                }
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E)
                ),
                enabled = !isLoading && !showSuccess
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Text("Changement...", color = Color.White)
                    }
                } else {
                    Text("Changer", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Annuler")
            }
        }
    )
}