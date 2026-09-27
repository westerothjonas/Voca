package com.example.voca.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface VocaRoute : NavKey {
    @Serializable
    data object Auth : VocaRoute

    @Serializable
    data object Uebersicht : VocaRoute

    @Serializable
    data object Lernen : VocaRoute

    @Serializable
    data object Pakete : VocaRoute

    @Serializable
    data object Einstellungen : VocaRoute

    @Serializable
    data class WordDetails(val itemId: String? = null) : VocaRoute
}
