package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class WordType(val labelGerman: String) {
    NOMEN("Nomen"),
    VERB("Verb"),
    ADJEKTIV("Adjektiv"),
    PHRASE("Phrase / Sonstiges")
}

@Serializable
enum class GrowthStage(
    val labelGerman: String,
    val emoji: String,
    val descriptionGerman: String
) {
    SAMEN("Samen", "🌱", "Neu gesät"),
    KEIMLING("Keimling", "🌿", "Im Wachstum"),
    KNOSPE("Knospe", "🌼", "Fast reif"),
    BLUEHEND("Blühend", "🌸", "Gemeistert")
}

@Serializable
data class VocabularyItem(
    val id: String = "",
    val baseForm: String = "",
    val wordType: WordType = WordType.VERB,
    val irregularForms: IrregularForms = IrregularForms(),
    val translation: String = "",          // Deutsche Übersetzung
    val phonetics: String = "",            // IPA Lautschrift
    val exampleSentence: String = "",      // Beispielsatz
    val exampleTranslation: String = "",   // Übersetzung Beispielsatz
    val mnemonic: String = "",             // Eselsbrücke
    val growthStage: GrowthStage = GrowthStage.SAMEN,
    val masteryStars: Int = 1,             // 1 bis 5 Sterne
    val vocabularyPack: String = "",       // Vokabel-Paket Name
    val packageId: String = "",            // Paket ID
    val studyPeriod: String = "1. Jan - 30. Jun",   // Lernzeitraum
    // SRS Spaced Repetition fields
    val interval: Int = 0,                 // in Tagen
    val repetition: Int = 0,
    val easeFactor: Double = 2.5,
    val nextReviewDate: Long = 0L,
    val userId: String = "default_user",
    val createdAt: Long = System.currentTimeMillis()
)
