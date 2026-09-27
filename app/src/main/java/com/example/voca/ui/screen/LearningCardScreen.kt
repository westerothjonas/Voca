package com.example.voca.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.data.model.SrsRating
import com.example.voca.ui.viewmodel.VocabularyViewModel
import com.example.voca.utils.MatchResult
import com.example.voca.utils.StringUtils

enum class LearningMode {
    STUDY,   // Flashcard Reveal Mode
    WRITE    // Schreibübung Mode
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningCardScreen(
    viewModel: VocabularyViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val items = if (uiState.filteredItems.isNotEmpty()) uiState.filteredItems else uiState.items

    var currentIndex by remember { mutableIntStateOf(0) }
    var currentMode by remember { mutableStateOf(LearningMode.STUDY) }
    var isRevealed by remember { mutableStateOf(false) }
    var isSessionFinished by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }

    if (items.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lernen", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Zurück", tint = Color(0xFF0F172A))
                        }
                    },
                    windowInsets = WindowInsets(0.dp),
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
                )
            },
            containerColor = Color(0xFFF1F5F9)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("🌱", fontSize = 48.sp)
                        Text(
                            text = "Keine Vokabeln vorhanden",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Erstelle ein Vokabel-Paket und füge Vokabeln hinzu, um hier zu lernen.",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Zurück zur Übersicht", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    // SESSION FINISHED SUMMARY SCREEN (Abmoderation)
    if (isSessionFinished) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lernsession beendet", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Zurück", tint = Color(0xFF0F172A))
                        }
                    },
                    windowInsets = WindowInsets(0.dp),
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
                )
            },
            containerColor = Color(0xFFF1F5F9)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("🎉", fontSize = 56.sp)
                        Text(
                            text = "Großartige Leistung!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Du hast alle Vokabeln dieser Session erfolgreich durchgearbeitet.",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Gegelernte Vokabeln:", color = Color(0xFF64748B), fontSize = 14.sp)
                                    Text("${items.size}", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Richtig gewusst:", color = Color(0xFF64748B), fontSize = 14.sp)
                                    Text("$correctCount von ${items.size}", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), fontSize = 14.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Aktueller Streak:", color = Color(0xFF64748B), fontSize = 14.sp)
                                    Text("${uiState.gardenState.streakDays} Tage 🔥", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                currentIndex = 0
                                correctCount = 0
                                isSessionFinished = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Nochmal lernen", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Zurück zur Übersicht", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                        }
                    }
                }
            }
        }
    } else {
        val currentItem = items.getOrElse(currentIndex) { items.first() }

        // Is this verb/item irregular?
        val isIrregular = currentItem.irregularForms.simplePast.isNotBlank() ||
            currentItem.irregularForms.pastParticiple.isNotBlank()

        // Auto-Focus Requester for Schreibübung text field
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(currentItem.id, currentIndex, currentMode) {
            if (currentMode == LearningMode.WRITE) {
                try { focusRequester.requestFocus() } catch (_: Exception) {}
            }
        }

        // User inputs for Schreibübung
        var mainInput by remember(currentItem.id, currentIndex) { mutableStateOf("") }
        var pastInput by remember(currentItem.id, currentIndex) { mutableStateOf("") }
        var partTwoInput by remember(currentItem.id, currentIndex) { mutableStateOf("") }

        var hasCheckedAnswer by remember(currentItem.id, currentIndex) { mutableStateOf(false) }

        val expectedBase = currentItem.baseForm.trim()
        val expectedInfinitiv = currentItem.irregularForms.infinitiv.ifBlank { expectedBase }.trim()
        val expectedPast = currentItem.irregularForms.simplePast.trim()
        val expectedParticiple = currentItem.irregularForms.pastParticiple.trim()

        // Typo Tolerance evaluation via Levenshtein Distance
        val mainResult = StringUtils.evaluateMatch(mainInput, expectedInfinitiv)
        val pastResult = if (isIrregular) StringUtils.evaluateMatch(pastInput, expectedPast) else MatchResult.EXACT
        val partResult = if (isIrregular) StringUtils.evaluateMatch(partTwoInput, expectedParticiple) else MatchResult.EXACT

        val isExact = mainResult == MatchResult.EXACT && pastResult == MatchResult.EXACT && partResult == MatchResult.EXACT
        val hasTypo = !isExact && (mainResult != MatchResult.WRONG && pastResult != MatchResult.WRONG && partResult != MatchResult.WRONG)
        val isOverallValid = isExact || hasTypo

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (currentMode == LearningMode.WRITE) "Schreibübung" else "Lernen",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (currentMode == LearningMode.WRITE) "Aufgabe ${currentIndex + 1} von ${items.size}" else "Karte ${currentIndex + 1} von ${items.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = if (currentMode == LearningMode.WRITE) Icons.Rounded.Close else Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Zurück",
                                    tint = Color(0xFF0F172A)
                                )
                            }
                        },
                        windowInsets = WindowInsets(0.dp),
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
                    )

                    // Top Progress Bar
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / items.size.coerceAtLeast(1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = Color(0xFF2563EB),
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            },
            bottomBar = {
                // Bottom Sub-navigation Tabs (Only Study & Write)
                Surface(
                    color = Color.White,
                    tonalElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tab 1: Study
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    currentMode = LearningMode.STUDY
                                    isRevealed = false
                                }
                                .padding(horizontal = 24.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Style,
                                contentDescription = "Study",
                                tint = if (currentMode == LearningMode.STUDY) Color(0xFF2563EB) else Color(0xFF64748B),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Lernen",
                                fontSize = 12.sp,
                                fontWeight = if (currentMode == LearningMode.STUDY) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentMode == LearningMode.STUDY) Color(0xFF2563EB) else Color(0xFF64748B)
                            )
                        }

                        // Tab 2: Write
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    currentMode = LearningMode.WRITE
                                    hasCheckedAnswer = false
                                }
                                .padding(horizontal = 24.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = "Write",
                                tint = if (currentMode == LearningMode.WRITE) Color(0xFF2563EB) else Color(0xFF64748B),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Schreiben",
                                fontSize = 12.sp,
                                fontWeight = if (currentMode == LearningMode.WRITE) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentMode == LearningMode.WRITE) Color(0xFF2563EB) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            },
            containerColor = Color(0xFFF1F5F9)
        ) { innerPadding ->
            if (currentMode == LearningMode.WRITE) {
                // Schreibübung Mode
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card 1: Prompt
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
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "BEDEUTUNG & ÜBERSETZUNG",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        color = Color(0xFF64748B)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFEFF6FF)
                                    ) {
                                        Text(
                                            text = if (isIrregular) "Unregelmäßiges Verb" else currentItem.vocabularyPack.ifBlank { "Vokabel" },
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2563EB)
                                        )
                                    }
                                }

                                Text(
                                    text = currentItem.translation.ifBlank { "Übersetzung" },
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                Text(
                                    text = if (isIrregular) "Trage den englischen Begriff und alle Stammformen ein." else "Trage das passende englische Wort ein.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Section 1: Englisches Hauptwort
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Englisches Wort",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            OutlinedTextField(
                                value = mainInput,
                                onValueChange = { if (!hasCheckedAnswer) mainInput = it },
                                placeholder = { Text("z.B. ${expectedBase.ifBlank { "speak" }}", color = Color(0xFFCBD5E1)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }

                    // Section 2: Zeitformen (Stammformen) - ONLY IF IRREGULAR VERB!
                    if (isIrregular) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Zeitformen (Stammformen)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Simple Past
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("1. Simple Past", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                            OutlinedTextField(
                                                value = pastInput,
                                                onValueChange = { if (!hasCheckedAnswer) pastInput = it },
                                                placeholder = { Text("Simple Past", color = Color(0xFFCBD5E1)) },
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2563EB))
                                            )
                                        }

                                        // Past Participle
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("2. Past Participle", fontSize = 11.sp, color = Color(0xFF64748B))
                                            OutlinedTextField(
                                                value = partTwoInput,
                                                onValueChange = { if (!hasCheckedAnswer) partTwoInput = it },
                                                placeholder = { Text("Partizip II", color = Color(0xFFCBD5E1)) },
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // DIRECT FEEDBACK & DETAILED SOLUTION CARD
                    if (hasCheckedAnswer) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isExact -> Color(0xFFDCFCE7)
                                        hasTypo -> Color(0xFFFEF3C7)
                                        else -> Color(0xFFFEE2E2)
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        isExact -> Color(0xFF86EFAC)
                                        hasTypo -> Color(0xFFFDE68A)
                                        else -> Color(0xFFFCA5A5)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(if (isExact) "✅" else if (hasTypo) "⚠️" else "❌", fontSize = 20.sp)
                                        Text(
                                            text = when {
                                                isExact -> "Perfekt! Exakt richtig!"
                                                hasTypo -> "Fast richtig (Kleine Abweichung)!"
                                                else -> "Leider nicht ganz richtig"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = when {
                                                isExact -> Color(0xFF16A34A)
                                                hasTypo -> Color(0xFFD97706)
                                                else -> Color(0xFFDC2626)
                                            }
                                        )
                                    }

                                    HorizontalDivider(
                                        color = when {
                                            isExact -> Color(0xFF86EFAC)
                                            hasTypo -> Color(0xFFFDE68A)
                                            else -> Color(0xFFFCA5A5)
                                        }
                                    )

                                    Text("Richtige Lösung:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    Text("• Englisches Wort: $expectedBase", fontSize = 13.sp, color = Color(0xFF0F172A))
                                    if (isIrregular) {
                                        Text("• Simple Past: $expectedPast", fontSize = 13.sp, color = Color(0xFF0F172A))
                                        Text("• Past Participle: $expectedParticiple", fontSize = 13.sp, color = Color(0xFF0F172A))
                                    }
                                }
                            }
                        }
                    }

                    // Action Button (Check or Proceed)
                    item {
                        if (!hasCheckedAnswer) {
                            Button(
                                onClick = {
                                    hasCheckedAnswer = true
                                    if (isOverallValid) {
                                        correctCount++
                                        viewModel.reviewItemSrs(currentItem, SrsRating.GOOD)
                                    } else {
                                        viewModel.reviewItemSrs(currentItem, SrsRating.AGAIN)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2563EB),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text("Antwort überprüfen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    hasCheckedAnswer = false
                                    if (currentIndex < items.size - 1) {
                                        currentIndex++
                                    } else {
                                        isSessionFinished = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2563EB),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Nächste Vokabel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                // Flashcard Study / Card Reveal Mode
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Main Flashcard (Scrollable content inside)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { isRevealed = !isRevealed },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Card Top Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentItem.wordType.labelGerman.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = Color(0xFF64748B)
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (isRevealed) Color(0xFF16A34A) else Color(0xFFF59E0B))
                                        )
                                        Text(
                                            text = if (isRevealed) "Aufgedeckt" else "Tippe zum Aufdecken",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))

                            // Card Main Body
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Auto-Sizing Base Form Title for long words!
                                val adaptiveFontSize = when {
                                    currentItem.baseForm.length > 20 -> 22.sp
                                    currentItem.baseForm.length > 14 -> 26.sp
                                    currentItem.baseForm.length > 9 -> 30.sp
                                    else -> 36.sp
                                }

                                Text(
                                    text = currentItem.baseForm,
                                    fontSize = adaptiveFontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center
                                )

                                if (isRevealed) {
                                    // Translation & Irregular verbs revealed on click!
                                    Text(
                                        text = currentItem.translation,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB),
                                        textAlign = TextAlign.Center
                                    )

                                    if (isIrregular) {
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Stammformen Container Box
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                // 1. Infinitiv
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.White,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text("1. Infinitiv", fontSize = 11.sp, color = Color(0xFF64748B))
                                                        Text(
                                                            currentItem.irregularForms.infinitiv.ifBlank { currentItem.baseForm },
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                    }
                                                }

                                                // 2. Simple Past
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.White,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text("2. Simple Past", fontSize = 11.sp, color = Color(0xFF64748B))
                                                        Text(
                                                            currentItem.irregularForms.simplePast.ifBlank { "-" },
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                    }
                                                }

                                                // 3. Past Participle
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.White,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text("3. Past Participle", fontSize = 11.sp, color = Color(0xFF64748B))
                                                        Text(
                                                            currentItem.irregularForms.pastParticiple.ifBlank { "-" },
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                    }
                                                }

                                                // Optional Deutsche Stammformen
                                                if (currentItem.irregularForms.germanForms.isNotBlank()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFEFF6FF),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Column(modifier = Modifier.padding(8.dp)) {
                                                            Text("Deutsche Stammformen", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                                                            Text(
                                                                currentItem.irregularForms.germanForms,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                color = Color(0xFF0F172A)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Hidden state prompt
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (isIrregular) "🔒 Übersetzung & Zeitformen verborgen" else "🔒 Übersetzung verborgen",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF94A3B8),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bottom Tip
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isRevealed) "Tippe zum Verstecken" else "Tippe zum Aufdecken",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Binary Rating Buttons: "Kannte ich nicht" & "Kannte ich"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Button 1: Kannte ich nicht (Red / Again)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.reviewItemSrs(currentItem, SrsRating.AGAIN)
                                    isRevealed = false
                                    if (currentIndex < items.size - 1) {
                                        currentIndex++
                                    } else {
                                        isSessionFinished = true
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("❌", fontSize = 20.sp)
                                Text("Kannte ich nicht", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFDC2626))
                            }
                        }

                        // Button 2: Kannte ich (Green / Good)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    correctCount++
                                    viewModel.reviewItemSrs(currentItem, SrsRating.GOOD)
                                    isRevealed = false
                                    if (currentIndex < items.size - 1) {
                                        currentIndex++
                                    } else {
                                        isSessionFinished = true
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("✅", fontSize = 20.sp)
                                Text("Kannte ich", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            }
        }
    }
}
