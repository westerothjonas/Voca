package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VocaBackupData(
    val packages: List<VocabPackage>,
    val items: List<VocabularyItem>
)
