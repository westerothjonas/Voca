package com.example.voca.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.data.model.IrregularForms
import com.example.voca.data.model.VocabPackage
import com.example.voca.data.model.VocabularyItem
import com.example.voca.ui.viewmodel.VocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVocabScreen(
    viewModel: VocabularyViewModel,
    itemId: String? = null,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val existingItem = remember(itemId, uiState.items) {
        uiState.items.find { it.id == itemId }
    }

    var baseForm by remember { mutableStateOf(existingItem?.baseForm ?: "") }
    var translation by remember { mutableStateOf(existingItem?.translation ?: "") }
    var infinitiv by remember { mutableStateOf(existingItem?.irregularForms?.infinitiv ?: "") }
    var simplePast by remember { mutableStateOf(existingItem?.irregularForms?.simplePast ?: "") }
    var pastParticiple by remember { mutableStateOf(existingItem?.irregularForms?.pastParticiple ?: "") }
    var germanForms by remember { mutableStateOf(existingItem?.irregularForms?.germanForms ?: "") }

    val availablePacks = remember(uiState.vocabPackages) {
        uiState.vocabPackages.map { it.title }.filter { it.isNotBlank() }.distinct()
    }

    var selectedPackage by remember(availablePacks, existingItem) {
        mutableStateOf(existingItem?.vocabularyPack.takeIf { !it.isNullOrBlank() } ?: availablePacks.firstOrNull() ?: "")
    }

    // Dialog state for creating a package right inside AddVocabScreen
    var isCreatingPackage by remember { mutableStateOf(false) }
    var newPackageTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (itemId == null) "Neue Vokabel" else "Vokabel bearbeiten",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val assignedPack = selectedPackage.ifBlank { "Allgemein" }
                            val matchingPkg = uiState.vocabPackages.find { it.title.equals(assignedPack, ignoreCase = true) }

                            // Primary system (baseForm / translation) with fallback to secondary system (infinitiv / germanForms)
                            val finalBaseForm = baseForm.ifBlank { infinitiv }.ifBlank { "Unbenannt" }
                            val finalTranslation = translation.ifBlank { germanForms }.ifBlank { "Übersetzung" }

                            val itemToSave = (existingItem ?: VocabularyItem()).copy(
                                baseForm = finalBaseForm,
                                translation = finalTranslation,
                                irregularForms = IrregularForms(
                                    infinitiv = infinitiv.ifBlank { finalBaseForm },
                                    simplePast = simplePast,
                                    pastParticiple = pastParticiple,
                                    germanForms = germanForms
                                ),
                                vocabularyPack = assignedPack,
                                packageId = matchingPkg?.id ?: ""
                            )
                            viewModel.saveItem(itemToSave) {
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Speichern", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
            )
        },
        containerColor = Color(0xFFF1F5F9)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: Vokabel (Primary System)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Vokabel (Primär)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        OutlinedTextField(
                            value = baseForm,
                            onValueChange = { baseForm = it },
                            placeholder = { Text("z.B. Ephemeral (oder nutze unten die Stammformen)", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }

            // Card 2: Bedeutung & Übersetzung (Primary System)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Translate,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Bedeutung & Übersetzung (Primär)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        OutlinedTextField(
                            value = translation,
                            onValueChange = { translation = it },
                            placeholder = { Text("Flüchtig, vergänglich", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }

            // Card 3: Unregelmäßige Stammformen (Secondary System)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Unregelmäßige Stammformen (Sekundär - Optional)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        // Form 1: Infinitiv
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("1. Infinitiv (Grundform)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                            OutlinedTextField(
                                value = infinitiv,
                                onValueChange = { infinitiv = it },
                                placeholder = { Text("z.B. go", color = Color(0xFFCBD5E1)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }

                        // Form 2 & 3 in a Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("2. Simple Past (Präteritum)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                OutlinedTextField(
                                    value = simplePast,
                                    onValueChange = { simplePast = it },
                                    placeholder = { Text("z.B. went", color = Color(0xFFCBD5E1)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF2563EB),
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    )
                                )
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("3. Past Participle (Perfekt)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                OutlinedTextField(
                                    value = pastParticiple,
                                    onValueChange = { pastParticiple = it },
                                    placeholder = { Text("z.B. gone", color = Color(0xFFCBD5E1)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF2563EB),
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    )
                                )
                            }
                        }

                        // Optional Field: Deutsche Stammformen / Deutsche Form
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Deutsche Stammformen (optional)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = germanForms,
                                onValueChange = { germanForms = it },
                                placeholder = { Text("z.B. gehen, ging, gegangen", color = Color(0xFFCBD5E1)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }
                }
            }

            // Card 4: Lernpaket zuweisen (ONLY user-created packages!)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Folder,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Lernpaket zuweisen",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            TextButton(
                                onClick = {
                                    newPackageTitle = ""
                                    isCreatingPackage = true
                                }
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Neues Paket", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                        }

                        if (availablePacks.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Noch kein Paket vorhanden. Erstelle erst ein Paket!",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = {
                                            newPackageTitle = ""
                                            isCreatingPackage = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("+ Erstellen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(availablePacks) { packName ->
                                    val isSelected = selectedPackage == packName
                                    Surface(
                                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable { selectedPackage = packName },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                                        border = BorderStroke(1.5.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = packName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFF334155)
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Rounded.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 5: Lernstatus & Wiederholung
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Autorenew,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Lernstatus & Wiederholung",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        // Inner Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFEFF6FF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.School,
                                                contentDescription = null,
                                                tint = Color(0xFF2563EB),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = "Stufe 1 (Neu)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    Text(
                                        text = "Neu gelernt",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { 0.25f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF2563EB),
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Create Package Dialog
    if (isCreatingPackage) {
        AlertDialog(
            onDismissRequest = { isCreatingPackage = false },
            title = { Text("Neues Paket erstellen", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newPackageTitle,
                    onValueChange = { newPackageTitle = it },
                    label = { Text("Paket-Titel (z.B. Spanisch B1)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPackageTitle.isNotBlank()) {
                            val newPkg = VocabPackage(
                                title = newPackageTitle.trim()
                            )
                            viewModel.saveVocabPackage(newPkg) {
                                selectedPackage = newPackageTitle.trim()
                                isCreatingPackage = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Erstellen", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreatingPackage = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
