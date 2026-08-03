package com.citoyen.jeparticipe.ui.citoyen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.citoyen.jeparticipe.data.location.LocationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

enum class Categorie {
    ECLAIRAGE_PUBLIC,
    VOIRIE,
    ASSAINISSEMENT,
    EAU,
    SECURITE,
    SANTE,
    EDUCATION,
    ENVIRONNEMENT,
    AUTRE
}

@Composable
fun CreateSignalementScreen(
    viewModel: SignalementViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // États des champs
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var categorie by remember { mutableStateOf(Categorie.AUTRE.name) }
    var expanded by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBase64 by remember { mutableStateOf<String?>(null) }
    var isPhotoLoading by remember { mutableStateOf(false) }

    // Permissions
    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // GPS
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    // État de chargement et messages
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
            isPhotoLoading = true
            try {
                photoBase64 = uriToBase64(context, uri, maxWidth = 1024, quality = 80)
            } catch (e: Exception) {
                errorMessage = "Erreur traitement image"
            } finally {
                isPhotoLoading = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            isPhotoLoading = true
            try {
                val uri = photoUri!!
                photoBase64 = uriToBase64(context, uri, maxWidth = 1024, quality = 80)
            } catch (e: Exception) {
                errorMessage = "Erreur traitement photo"
            } finally {
                isPhotoLoading = false
            }
        } else {
            photoUri = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            val uri = createImageUri(context)
            photoUri = uri
            cameraLauncher.launch(uri)
        } else {
            errorMessage = "Permission caméra refusée"
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val location = locationManager.getCurrentLocation()
            latitude = location?.first
            longitude = location?.second
        }
    }

    fun resetForm() {
        titre = ""
        description = ""
        adresse = ""
        categorie = Categorie.AUTRE.name
        photoUri = null
        photoBase64 = null
        isLoading = false
        showSuccess = true
        errorMessage = null
    }

    // Le corps avec défilement
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // En-tête fixe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📝 Créer signalement",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Retour", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zone défilante pour le formulaire
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Prend tout l'espace restant
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Titre
                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A237E)
                    )
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A237E)
                    )
                )

                // Adresse
                OutlinedTextField(
                    value = adresse,
                    onValueChange = { adresse = it },
                    label = { Text("Adresse *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A237E)
                    )
                )

                // GPS
                Surface(
                    color = Color(0xFFE8EAF6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (latitude != null)
                            "📍 GPS: $latitude , $longitude"
                        else
                            "📍 Recherche GPS...",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF1A237E)
                    )
                }

                // Catégorie
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Catégorie : $categorie")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Categorie.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(it.name) },
                                onClick = {
                                    categorie = it.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Boutons photo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📷 Galerie")
                    }

                    OutlinedButton(
                        onClick = {
                            if (hasCameraPermission) {
                                val uri = createImageUri(context)
                                photoUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📸 Caméra")
                    }
                }

                // Indicateur de chargement photo
                if (isPhotoLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Traitement de l'image...", color = Color.Gray)
                    }
                }

                // Affichage photo sélectionnée
                if (photoBase64 != null && !isPhotoLoading) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅ Photo (${photoBase64!!.length / 1024} Ko)",
                                color = Color(0xFF2E7D32),
                                fontSize = 14.sp
                            )
                            TextButton(
                                onClick = {
                                    photoUri = null
                                    photoBase64 = null
                                }
                            ) {
                                Text("Retirer", color = Color(0xFFEF5350))
                            }
                        }
                    }
                }

                // Messages
                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFFEF5350),
                            fontSize = 14.sp
                        )
                    }
                }

                if (showSuccess) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✅ Signalement créé avec succès !",
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bouton Envoyer (toujours visible en bas du formulaire)
                Button(
                    onClick = {
                        if (titre.isNotBlank() && description.isNotBlank() && adresse.isNotBlank()) {
                            isLoading = true
                            errorMessage = null

                            viewModel.createSignalementWithPhoto(
                                titre = titre,
                                description = description,
                                latitude = latitude,
                                longitude = longitude,
                                adresse = adresse,
                                categorie = categorie,
                                photoBase64 = photoBase64,
                                onSuccess = {
                                    isLoading = false
                                    showSuccess = true
                                    coroutineScope.launch {
                                        delay(3000)
                                        resetForm()
                                    }
                                },
                                onError = { error ->
                                    isLoading = false
                                    errorMessage = error
                                }
                            )
                        } else {
                            errorMessage = "Veuillez remplir tous les champs obligatoires"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A237E)
                    ),
                    enabled = !isLoading && !isPhotoLoading
                ) {
                    if (isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text("Envoi en cours...", color = Color.White)
                        }
                    } else {
                        Text(
                            text = "Envoyer le signalement",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Fonctions utilitaires (inchangées)
fun uriToBase64(context: Context, uri: Uri, maxWidth: Int = 1024, quality: Int = 80): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        val width = options.outWidth
        val height = options.outHeight
        var sampleSize = 1
        while (width / sampleSize > maxWidth && height / sampleSize > maxWidth) {
            sampleSize *= 2
        }

        val newOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val inputStream2 = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream2, null, newOptions)
        inputStream2?.close()

        if (bitmap == null) return null

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val bytes = outputStream.toByteArray()
        bitmap.recycle()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun createImageUri(context: Context): Uri {
    val directory = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "signalements"
    )
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, "IMG_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}