package com.citoyen.jeparticipe.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.data.model.User
import com.citoyen.jeparticipe.ui.common.CommentairesDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdminHomeScreen(
    viewModel: AdminViewModel = viewModel(),
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Signalements", "Utilisateurs")

    var showCommentairesDialog by remember { mutableStateOf(false) }
    var selectedSignalement by remember { mutableStateOf<Signalement?>(null) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }  // ✅ AJOUT

    LaunchedEffect(Unit) {
        viewModel.chargerSignalements()
        viewModel.chargerUtilisateurs()
    }

    val stats = viewModel.getStats()
    val isLoadingSignalements = viewModel.isLoadingSignalements.value
    val isLoadingUsers = viewModel.isLoadingUsers.value

    val filteredSignalements = when (selectedFilter) {
        "EN_ATTENTE" -> viewModel.signalements.filter { it.statut?.uppercase() == "EN_ATTENTE" }
        "EN_COURS" -> viewModel.signalements.filter { it.statut?.uppercase() == "EN_COURS" }
        "RESOLU" -> viewModel.signalements.filter { it.statut?.uppercase() == "RESOLU" }
        else -> viewModel.signalements
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // En-tête
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A237E)
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "👑 Admin",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Gestion complète",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ✅ BOUTON PROFIL
                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = Color.White
                        )
                    }

                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEBEE).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .width(120.dp)
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFEF5350)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Déconnexion",
                            color = Color(0xFFEF5350),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Statistiques cliquables
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                StatItemAdminClickable(
                    count = stats.totalSignalements,
                    label = "Total",
                    color = Color(0xFF1A237E),
                    isSelected = selectedFilter == null,
                    onClick = { selectedFilter = null }
                )
                StatItemAdminClickable(
                    count = stats.enAttente,
                    label = "En attente",
                    color = Color(0xFFFFA726),
                    isSelected = selectedFilter == "EN_ATTENTE",
                    onClick = {
                        selectedFilter = if (selectedFilter == "EN_ATTENTE") null else "EN_ATTENTE"
                    }
                )
                StatItemAdminClickable(
                    count = stats.enCours,
                    label = "En cours",
                    color = Color(0xFF42A5F5),
                    isSelected = selectedFilter == "EN_COURS",
                    onClick = {
                        selectedFilter = if (selectedFilter == "EN_COURS") null else "EN_COURS"
                    }
                )
                StatItemAdminClickable(
                    count = stats.resolus,
                    label = "Résolus",
                    color = Color(0xFF66BB6A),
                    isSelected = selectedFilter == "RESOLU",
                    onClick = {
                        selectedFilter = if (selectedFilter == "RESOLU") null else "RESOLU"
                    }
                )
            }
        }

        if (selectedFilter != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE8EAF6),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔍 Filtre: ${when (selectedFilter) {
                                "EN_ATTENTE" -> "En attente"
                                "EN_COURS" -> "En cours"
                                "RESOLU" -> "Résolus"
                                else -> selectedFilter ?: ""
                            }}",
                            fontSize = 13.sp,
                            color = Color(0xFF1A237E),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { selectedFilter = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Supprimer filtre",
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF1A237E)
                            )
                        }
                    }
                }
                Text(
                    text = "${filteredSignalements.size} signalement(s)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                if (isLoadingSignalements) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Chargement des signalements...", color = Color.Gray)
                        }
                    }
                } else {
                    AdminSignalementsList(
                        signalements = filteredSignalements,
                        onUpdateStatut = { signalement, statut ->
                            viewModel.updateStatut(signalement, statut)
                        },
                        onCommentairesClick = { signalement ->
                            selectedSignalement = signalement
                            viewModel.chargerCommentaires(signalement.id!!)
                            showCommentairesDialog = true
                        },
                        onDeleteClick = { signalement ->  // ✅ AJOUT
                            viewModel.deleteSignalement(signalement)
                        }
                    )
                }
            }
            1 -> {
                if (isLoadingUsers) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Chargement des utilisateurs...", color = Color.Gray)
                        }
                    }
                } else {
                    AdminUsersList(
                        users = viewModel.users,
                        onUpdateRole = { user, role ->
                            viewModel.updateRole(user, role)
                        },
                        onToggleStatus = { user ->
                            viewModel.toggleUserStatus(user)
                        },
                        onDeleteUser = { user ->
                            viewModel.deleteUser(user)
                        },
                        onCreateUser = { nom, prenom, email, password, telephone ->
                            viewModel.createUser(nom, prenom, email, password, telephone)
                        }
                    )
                }
            }
        }
    }

    // Dialog des commentaires
    if (showCommentairesDialog && selectedSignalement != null) {
        CommentairesDialog(
            signalementId = selectedSignalement!!.id!!,
            commentaires = viewModel.commentaires,
            isAgentOrAdmin = true,
            isLoading = viewModel.isLoadingCommentaires.value,
            onAjouterCommentaire = { contenu, estJustification ->
                viewModel.ajouterCommentaire(selectedSignalement!!.id!!, contenu, estJustification)
            },
            onSupprimerCommentaire = { commentaireId ->
                viewModel.supprimerCommentaire(commentaireId)
            },
            onMarquerCommeLu = { commentaireId ->
                viewModel.marquerCommeLu(commentaireId)
            },
            onMarquerTousCommeLus = {
                viewModel.marquerTousCommeLus(selectedSignalement!!.id!!)
            },
            onDismiss = {
                showCommentairesDialog = false
                selectedSignalement = null
            }
        )
    }

    // ✅ DIALOG PROFIL - CHANGER MOT DE PASSE
    if (showProfileDialog) {
        ChangePasswordDialog(
            viewModel = viewModel,
            onDismiss = { showProfileDialog = false }
        )
    }

    // Dialog de confirmation de déconnexion
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Déconnexion",
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Êtes-vous sûr de vouloir vous déconnecter ?")
            },
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
                    Text("Annuler", color = Color(0xFF1A237E))
                }
            }
        )
    }
}

// ✅ COMPOSANT STATISTIQUES CLIQUABLE
@Composable
fun StatItemAdminClickable(
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

// ✅ DIALOG CHANGER MOT DE PASSE
@Composable
fun ChangePasswordDialog(
    viewModel: AdminViewModel,
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
                                onError = { error ->
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