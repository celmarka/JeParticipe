package com.citoyen.jeparticipe.ui.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citoyen.jeparticipe.data.model.Signalement
import com.citoyen.jeparticipe.utils.DateUtils

@Composable
fun AdminSignalementsList(
    signalements: List<Signalement>,
    onUpdateStatut: (Signalement, String) -> Unit,
    onCommentairesClick: (Signalement) -> Unit,
    onDeleteClick: (Signalement) -> Unit,
    onAssignClick: (Signalement) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var sortByDate by remember { mutableStateOf(true) }

    val filteredSignalements = if (searchText.isNotBlank()) {
        signalements.filter {
            it.titre.contains(searchText, ignoreCase = true) ||
                    it.description?.contains(searchText, ignoreCase = true) == true
        }
    } else {
        signalements
    }

    val sortedSignalements = if (sortByDate) {
        DateUtils.sortByDateDesc(filteredSignalements)
    } else {
        filteredSignalements
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.weight(1f).height(56.dp),
                placeholder = { Text("Rechercher un signalement...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Rechercher", modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Effacer", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1A237E),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )

            IconButton(
                onClick = { sortByDate = !sortByDate },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (sortByDate) Color(0xFF1A237E) else Color.White,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Trier par date",
                    modifier = Modifier.size(24.dp),
                    tint = if (sortByDate) Color.White else Color(0xFF1A237E)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📋 Signalements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A237E), fontSize = 15.sp)
            Text("${sortedSignalements.size} signalement(s)", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (sortedSignalements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(50), color = Color(0xFFE8EAF6)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = if (searchText.isNotEmpty()) "🔍" else "📭", fontSize = 40.sp)
                        }
                    }
                    Text(if (searchText.isNotEmpty()) "Aucun résultat" else "Aucun signalement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                    if (searchText.isNotEmpty()) {
                        Text("Aucun signalement ne correspond à votre recherche", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sortedSignalements) { signalement ->
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                        AdminSignalementCard(
                            signalement = signalement,
                            onUpdateStatut = { statut -> onUpdateStatut(signalement, statut) },
                            onCommentairesClick = { onCommentairesClick(signalement) },
                            onDeleteClick = { onDeleteClick(signalement) },
                            onAssignClick = { onAssignClick(signalement) }
                        )
                    }
                }
            }
        }
    }
}