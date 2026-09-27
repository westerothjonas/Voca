package com.example.voca

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.voca.ui.navigation.VocaRoute
import com.example.voca.ui.screen.AddVocabScreen
import com.example.voca.ui.screen.AuthScreen
import com.example.voca.ui.screen.EinstellungenScreen
import com.example.voca.ui.screen.LearningCardScreen
import com.example.voca.ui.screen.OverviewScreen
import com.example.voca.ui.screen.PackageOverviewScreen
import com.example.voca.ui.theme.VocaTheme
import com.example.voca.ui.viewmodel.VocabularyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VocabularyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Match status bar color to app background & top app bar (#F1F5F9)
        window.statusBarColor = Color.parseColor("#F1F5F9")
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            VocaTheme(darkTheme = false) {
                val context = LocalContext.current
                
                // Request notification permission on Android 13+
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    viewModel.startReminderService(context)
                }

                val isInitialSignedIn = remember { viewModel.isUserSignedIn }
                val initialRoute = if (isInitialSignedIn) VocaRoute.Uebersicht else VocaRoute.Auth

                val backStack = remember { mutableStateListOf<Any>(initialRoute) }
                val currentRoute = backStack.lastOrNull() ?: initialRoute

                val isTopLevelScreen = currentRoute is VocaRoute.Uebersicht ||
                    currentRoute is VocaRoute.Lernen ||
                    currentRoute is VocaRoute.Pakete ||
                    currentRoute is VocaRoute.Einstellungen

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isTopLevelScreen) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 4.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute is VocaRoute.Uebersicht,
                                    onClick = {
                                        if (currentRoute !is VocaRoute.Uebersicht) {
                                            backStack.clear()
                                            backStack.add(VocaRoute.Uebersicht)
                                        }
                                    },
                                    icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Übersicht") },
                                    label = { Text("Übersicht", fontSize = 11.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute is VocaRoute.Lernen,
                                    onClick = {
                                        if (currentRoute !is VocaRoute.Lernen) {
                                            backStack.clear()
                                            backStack.add(VocaRoute.Lernen)
                                        }
                                    },
                                    icon = { Icon(Icons.Rounded.School, contentDescription = "Lernen") },
                                    label = { Text("Lernen", fontSize = 11.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute is VocaRoute.Pakete,
                                    onClick = {
                                        if (currentRoute !is VocaRoute.Pakete) {
                                            backStack.clear()
                                            backStack.add(VocaRoute.Pakete)
                                        }
                                    },
                                    icon = { Icon(Icons.Rounded.Folder, contentDescription = "Pakete") },
                                    label = { Text("Pakete", fontSize = 11.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute is VocaRoute.Einstellungen,
                                    onClick = {
                                        if (currentRoute !is VocaRoute.Einstellungen) {
                                            backStack.clear()
                                            backStack.add(VocaRoute.Einstellungen)
                                        }
                                    },
                                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Einstellungen") },
                                    label = { Text("Einstellungen", fontSize = 10.5.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.padding(innerPadding),
                        entryProvider = { key ->
                            when (key) {
                                is VocaRoute.Auth -> NavEntry(key) {
                                    AuthScreen(
                                        viewModel = viewModel,
                                        onAuthSuccess = {
                                            backStack.clear()
                                            backStack.add(VocaRoute.Uebersicht)
                                        }
                                    )
                                }
                                is VocaRoute.Uebersicht -> NavEntry(key) {
                                    OverviewScreen(
                                        viewModel = viewModel,
                                        onNavigateToFlashcards = { backStack.add(VocaRoute.Lernen) },
                                        onNavigateToPakete = { backStack.add(VocaRoute.Pakete) },
                                        onNavigateToAddWord = { backStack.add(VocaRoute.WordDetails(null)) },
                                        onNavigateToEditWord = { id -> backStack.add(VocaRoute.WordDetails(id)) }
                                    )
                                }
                                is VocaRoute.Lernen -> NavEntry(key) {
                                    LearningCardScreen(
                                        viewModel = viewModel,
                                        onBack = { backStack.removeLastOrNull() }
                                    )
                                }
                                is VocaRoute.Pakete -> NavEntry(key) {
                                    PackageOverviewScreen(
                                        viewModel = viewModel,
                                        onNavigateToFlashcards = { backStack.add(VocaRoute.Lernen) },
                                        onNavigateToAddWord = { backStack.add(VocaRoute.WordDetails(null)) }
                                    )
                                }
                                is VocaRoute.Einstellungen -> NavEntry(
                                    key
                                ) {
                                    EinstellungenScreen(
                                        viewModel = viewModel,
                                        onNavigateToPakete = { backStack.add(VocaRoute.Pakete) },
                                        onSignOut = {
                                            viewModel.signOut()
                                            backStack.clear()
                                            backStack.add(VocaRoute.Auth)
                                        }
                                    )
                                }
                                is VocaRoute.WordDetails -> NavEntry(key) {
                                    AddVocabScreen(
                                        viewModel = viewModel,
                                        itemId = key.itemId,
                                        onBack = { backStack.removeLastOrNull() }
                                    )
                                }
                                else -> NavEntry(Unit) {
                                    Text("Unbekannte Route")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
