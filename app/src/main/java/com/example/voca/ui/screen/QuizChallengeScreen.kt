package com.example.voca.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.voca.ui.viewmodel.VocabularyViewModel

@Composable
fun QuizChallengeScreen(viewModel: VocabularyViewModel, onBack: () -> Unit = {}) {
    Text("Quiz Challenge")
}
