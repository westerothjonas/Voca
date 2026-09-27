package com.example.voca.ui.screen

import androidx.compose.runtime.Composable
import com.example.voca.ui.viewmodel.VocabularyViewModel

@Composable
fun FlashcardStudyScreen(
    viewModel: VocabularyViewModel,
    onBack: () -> Unit
) {
    LearningCardScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}
