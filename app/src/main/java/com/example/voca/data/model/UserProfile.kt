package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val base64Avatar: String = "", // Base64 encoded string of profile picture
    val dailyGoal: Int = 10,
    val streakDays: Int = 1,
    val lastStudyDate: String = "",
    val reminderEnabled: Boolean = true,
    val reminderTime: String = "19:00"
)
