package com.example.voca

import com.example.voca.data.model.GrowthStage
import com.example.voca.data.model.VocabularyItem
import com.example.voca.data.model.WordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VocabularyViewModelTest {

    @Test
    fun testVocabularyItemModel() {
        val item = VocabularyItem(
            id = "test-1",
            baseForm = "Serendipity",
            wordType = WordType.NOMEN,
            translation = "Glücklicher Zufall",
            phonetics = "/ˌser.ənˈdɪp.ə.ti/",
            exampleSentence = "Pure serendipity.",
            exampleTranslation = "Reiner Zufall.",
            mnemonic = "Test Eselsbrücke",
            growthStage = GrowthStage.BLUEHEND,
            masteryStars = 5,
            vocabularyPack = "C1 Akademisch",
            studyPeriod = "1. Jan - 30. Jun"
        )

        assertEquals("Serendipity", item.baseForm)
        assertEquals(WordType.NOMEN, item.wordType)
        assertEquals("Glücklicher Zufall", item.translation)
        assertEquals(GrowthStage.BLUEHEND, item.growthStage)
        assertEquals(5, item.masteryStars)
        assertEquals("C1 Akademisch", item.vocabularyPack)
    }

    @Test
    fun testGrowthStageProgression() {
        assertEquals("Samen", GrowthStage.SAMEN.labelGerman)
        assertEquals("🌱", GrowthStage.SAMEN.emoji)

        assertEquals("Keimling", GrowthStage.KEIMLING.labelGerman)
        assertEquals("🌿", GrowthStage.KEIMLING.emoji)

        assertEquals("Blühend", GrowthStage.BLUEHEND.labelGerman)
        assertEquals("🌸", GrowthStage.BLUEHEND.emoji)
    }
}
