package com.example.voca.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.data.model.VocabPackage
import com.example.voca.ui.viewmodel.VocabularyViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageOverviewScreen(
    viewModel: VocabularyViewModel,
    onNavigateToFlashcards: () -> Unit,
    onNavigateToAddWord: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val packages = uiState.vocabPackages
    val items = uiState.items

    var selectedFilter by remember { mutableStateOf("Aktuell") }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Selected package for drill-down view
    var selectedPackageDetail by remember { mutableStateOf<VocabPackage?>(null) }

    // Dialog states
    var isCreatingPackage by remember { mutableStateOf(false) }
    var newPackageTitle by remember { mutableStateOf("") }
    var newPackageStartDate by remember { mutableStateOf("1. Mär") }
    var newPackageEndDate by remember { mutableStateOf("31. Aug") }

    // Date Picker States
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    var editingPackage by remember { mutableStateOf<VocabPackage?>(null) }
    var editTitle by remember { mutableStateOf("") }

    val filteredPackages = remember(packages, searchQuery) {
        if (searchQuery.isBlank()) packages
        else packages.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    if (selectedPackageDetail != null) {
        // PACKAGE DETAIL / DRILL-DOWN VIEW
        val currentPkg = selectedPackageDetail!!
        val pkgItems = items.filter { it.vocabularyPack.equals(currentPkg.title, ignoreCase = true) || it.packageId == currentPkg.id }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentPkg.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${pkgItems.size} Vokabeln im Paket",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedPackageDetail = null }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Zurück", tint = Color(0xFF0F172A))
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                editingPackage = currentPkg
                                editTitle = currentPkg.title
                            }
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Bearbeiten", tint = Color(0xFF0F172A))
                        }
                        IconButton(
                            onClick = {
                                viewModel.deleteVocabPackage(currentPkg.id)
                                selectedPackageDetail = null
                            }
                        ) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Löschen", tint = Color(0xFFDC2626))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.setPackFilter(currentPkg.title)
                        onNavigateToAddWord()
                    },
                    icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                    text = { Text("Vokabel hinzufügen", fontWeight = FontWeight.Bold) },
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            },
            containerColor = Color(0xFFF1F5F9)
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Action Banner
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lernfortschritt",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${pkgItems.count { it.masteryStars >= 4 }} / ${pkgItems.size} gemeistert",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }

                            val progressFrac = if (pkgItems.isNotEmpty()) {
                                pkgItems.count { it.masteryStars >= 4 }.toFloat() / pkgItems.size
                            } else 0f

                            LinearProgressIndicator(
                                progress = { progressFrac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF2563EB),
                                trackColor = Color(0xFFE2E8F0)
                            )

                            Button(
                                onClick = {
                                    viewModel.setPackFilter(currentPkg.title)
                                    onNavigateToFlashcards()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.School, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dieses Paket jetzt lernen", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Vokabeln in diesem Paket (${pkgItems.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                if (pkgItems.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(28.dp)
                            ) {
                                Text("📚", fontSize = 36.sp)
                                Text("Noch keine Vokabeln im Paket", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text("Füge Schritt für Schritt Vokabeln zu diesem Paket hinzu.", fontSize = 13.sp, color = Color(0xFF64748B))
                                Button(
                                    onClick = {
                                        viewModel.setPackFilter(currentPkg.title)
                                        onNavigateToAddWord()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Erste Vokabel hinzufügen", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(pkgItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onNavigateToAddWord()
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(item.baseForm, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(item.translation, fontSize = 13.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    if (item.irregularForms.simplePast.isNotBlank()) {
                                        Text("Forms: ${item.irregularForms.simplePast} • ${item.irregularForms.pastParticiple}", fontSize = 11.sp, color = Color(0xFF2563EB), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteItem(item.id) }
                                ) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Löschen", tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // OVERVIEW LIST OF ALL PACKAGES
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Spa,
                                    contentDescription = "Voca Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Voca",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    },
                    actions = {
                        // Search Button
                        IconButton(
                            onClick = {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) searchQuery = ""
                            }
                        ) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Rounded.Close else Icons.Rounded.Search,
                                contentDescription = "Suchen",
                                tint = Color(0xFF0F172A)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        newPackageTitle = ""
                        isCreatingPackage = true
                    },
                    icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                    text = { Text("Neues Paket erstellen", fontWeight = FontWeight.Bold) },
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            },
            containerColor = Color(0xFFF1F5F9)
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Input Field
                item {
                    AnimatedVisibility(visible = isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Paket-Titel suchen...") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF2563EB)) },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Löschen", tint = Color(0xFF64748B))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                }

                // Header Section: Title & Slot Counter
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vokabel-Pakete",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "${packages.size} Pakete",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }

                // Filter Chips Row
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf("Aktuell", "Geplant", "Archiviert", "Alle")
                        items(filters) { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedFilter = filter },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) Color(0xFF2563EB) else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                    Text(
                                        text = filter,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }
                }

                if (filteredPackages.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text("📦", fontSize = 40.sp)
                                Text(
                                    text = if (searchQuery.isNotBlank()) "Kein Paket für '$searchQuery' gefunden" else "Noch kein Paket erstellt",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Erstelle dein erstes Vokabel-Paket, um Vokabeln hinzuzufügen und zu lernen.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Button(
                                    onClick = {
                                        newPackageTitle = ""
                                        isCreatingPackage = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Neues Paket erstellen", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Package Cards List (ONLY User Created Packages)
                    items(filteredPackages) { pkg ->
                        val pkgItems = items.filter { it.vocabularyPack.equals(pkg.title, ignoreCase = true) || it.packageId == pkg.id }
                        val totalCount = pkgItems.size
                        val masteredCount = pkgItems.count { it.masteryStars >= 4 }
                        val trainingCount = pkgItems.count { it.masteryStars in 2..3 }
                        val newCount = pkgItems.count { it.masteryStars <= 1 }

                        val progressFraction = if (totalCount > 0) masteredCount.toFloat() / totalCount else 0f
                        val progressPercent = (progressFraction * 100).toInt()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedPackageDetail = pkg },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Title Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.School,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = pkg.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0F172A),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFEFF6FF),
                                            modifier = Modifier.clickable {
                                                editingPackage = pkg
                                                editTitle = pkg.title
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Rounded.Edit, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                                Text("Umbenennen", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                            }
                                        }
                                    }
                                }

                                // Progress Info
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("$totalCount Vokabeln angelegt ($masteredCount gemeistert)", fontSize = 13.sp, color = Color(0xFF64748B))
                                        Text("$progressPercent%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                    }

                                    LinearProgressIndicator(
                                        progress = { progressFraction.coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFF2563EB),
                                        trackColor = Color(0xFFE2E8F0)
                                    )
                                }

                                // 3 Stat Columns Inner Box
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                                Text("$masteredCount", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                            }
                                            Text("Gemeistert", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }

                                        VerticalDivider(modifier = Modifier.height(28.dp), color = Color(0xFFE2E8F0))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                                Text("$trainingCount", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                            }
                                            Text("Im Training", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }

                                        VerticalDivider(modifier = Modifier.height(28.dp), color = Color(0xFFE2E8F0))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                                Text("$newCount", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                            }
                                            Text("Neu", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                    }
                                }

                                // Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.setPackFilter(pkg.title)
                                            onNavigateToFlashcards()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2563EB),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                    ) {
                                        Icon(Icons.Rounded.School, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Paket lernen", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    Button(
                                        onClick = { selectedPackageDetail = pkg },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF1F5F9),
                                            contentColor = Color(0xFF0F172A)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(46.dp)
                                    ) {
                                        Text("Öffnen →", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
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
    }

    // Create New Package Dialog (with Date Picker Calendar Integration)
    if (isCreatingPackage) {
        AlertDialog(
            onDismissRequest = { isCreatingPackage = false },
            title = { Text("Neues Paket erstellen", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newPackageTitle,
                        onValueChange = { newPackageTitle = it },
                        label = { Text("Paket-Titel (z.B. Spanisch B1)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Start Date Picker Field with Calendar Icon
                    OutlinedTextField(
                        value = newPackageStartDate,
                        onValueChange = { newPackageStartDate = it },
                        label = { Text("Startdatum") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(Icons.Rounded.DateRange, contentDescription = "Kalender öffnen", tint = Color(0xFF2563EB))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartDatePicker = true }
                    )

                    // End Date Picker Field with Calendar Icon
                    OutlinedTextField(
                        value = newPackageEndDate,
                        onValueChange = { newPackageEndDate = it },
                        label = { Text("Enddatum") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(Icons.Rounded.DateRange, contentDescription = "Kalender öffnen", tint = Color(0xFF2563EB))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndDatePicker = true }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPackageTitle.isNotBlank()) {
                            val newPkg = VocabPackage(
                                title = newPackageTitle.trim(),
                                startDate = newPackageStartDate.trim(),
                                endDate = newPackageEndDate.trim()
                            )
                            viewModel.saveVocabPackage(newPkg) {
                                isCreatingPackage = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Paket erstellen", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreatingPackage = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    // Start Date Picker Dialog
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            newPackageStartDate = localDate.format(formatter)
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // End Date Picker Dialog
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            newPackageEndDate = localDate.format(formatter)
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    // Edit Package Dialog
    if (editingPackage != null) {
        AlertDialog(
            onDismissRequest = { editingPackage = null },
            title = { Text("Paket umbenennen", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = { editTitle = it },
                    label = { Text("Titel des Pakets") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        editingPackage?.let { pkg ->
                            viewModel.saveVocabPackage(pkg.copy(title = editTitle.trim()))
                        }
                        editingPackage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Speichern")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPackage = null }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
