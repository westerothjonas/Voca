package com.example.voca.ui.screen

import androidx.compose.runtime.Composable
import com.example.voca.ui.viewmodel.VocabularyViewModel

@Composable
fun WordDetailsScreen(
    viewModel: VocabularyViewModel,
    itemId: String? = null,
    onBack: () -> Unit
) {
    AddVocabScreen(
        viewModel = viewModel,
        itemId = itemId,
        onBack = onBack
    )
}
