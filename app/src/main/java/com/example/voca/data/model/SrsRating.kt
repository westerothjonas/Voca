package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SrsRating(val labelGerman: String, val score: Int) {
    AGAIN("Again", 0),
    HARD("Schwer", 1),
    GOOD("Gut", 2),
    EASY("Einfach", 3)
}
