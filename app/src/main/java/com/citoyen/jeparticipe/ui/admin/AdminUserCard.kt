package com.citoyen.jeparticipe.ui.admin

import androidx.compose.foundation.layout.*
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
fun AdminUserCard(
    user: User,
    onUpdateRole: (String) -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("CITOYEN", "SERVICE_PUBLIC", "ADMIN")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Nom + Email + Statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${user.nom} ${user.prenom}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = user.email ?: "",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = if (user.actif == true) Color(0xFF66BB6A).copy(alpha = 0.15f) else Color(0xFFEF5350).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (user.actif == true) "✅ Actif" else "❌ Inactif",
                        fontSize = 10.sp,
                        color = if (user.actif == true) Color(0xFF66BB6A) else Color(0xFFEF5350),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rôle + Bouton Changer rôle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rôle: ${user.role ?: "Non défini"}",
                    fontSize = 13.sp,
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Medium
                )

                // Bouton Changer rôle plus petit
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .height(26.dp)
                        .width(100.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Changer rôle",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions - Boutons plus petits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Bouton Activer/Désactiver
                OutlinedButton(
                    onClick = onToggleStatus,
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (user.actif == true) Color(0xFFEF5350) else Color(0xFF66BB6A)
                    )
                ) {
                    Text(
                        text = if (user.actif == true) "Désactiver" else "Activer",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Bouton Supprimer
                Button(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Text(
                        text = "Supprimer",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Dropdown Menu pour les rôles
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = role,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            onUpdateRole(role)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}