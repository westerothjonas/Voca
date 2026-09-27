package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GardenState(
    val streakDays: Int = 1,
    val lastStudyDate: String = "" // format "yyyyMMdd"
)
