package com.citoyen.jeparticipe.ui.admin

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
import com.citoyen.jeparticipe.data.model.User

@Composable
fun AdminUsersList(
    users: List<User>,
    onUpdateRole: (User, String) -> Unit,
    onToggleStatus: (User) -> Unit,
    onDeleteUser: (User) -> Unit,
    onCreateUser: (String, String, String, String, String?) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<User?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Bouton Créer
        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A237E)
            )
        ) {
            Text(
                text = "➕ Créer un utilisateur",
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (users.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun utilisateur", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    AdminUserCard(
                        user = user,
                        onUpdateRole = { role ->
                            onUpdateRole(user, role)
                        },
                        onToggleStatus = {
                            onToggleStatus(user)
                        },
                        onDelete = {
                            showDeleteDialog = user
                        }
                    )
                }
            }
        }
    }

    // Dialog Création
    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { nom, prenom, email, password, telephone ->
                onCreateUser(nom, prenom, email, password, telephone)
                showCreateDialog = false
            }
        )
    }

    // Dialog Suppression
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Supprimer l'utilisateur") },
            text = {
                Text("Êtes-vous sûr de vouloir supprimer ${showDeleteDialog?.nom} ${showDeleteDialog?.prenom} ?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(showDeleteDialog!!)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}