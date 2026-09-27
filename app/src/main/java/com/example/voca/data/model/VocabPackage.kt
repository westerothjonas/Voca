package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VocabPackage(
    val id: String = "",
    val slotIndex: Int = 0, // 0, 1, or 2 (3 slots limit)
    val title: String = "",
    val startDate: String = "1. Jan",
    val endDate: String = "30. Jun",
    val wordCount: Int = 0,
    val userId: String = "default_user",
    val createdAt: Long = System.currentTimeMillis()
)
