package com.example.voca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IrregularForms(
    val infinitiv: String = "",
    val simplePast: String = "",
    val pastParticiple: String = "",
    val thirdPersonSingular: String = "",
    val germanForms: String = "" // Optional Deutsche Übersetzung der Stammformen (z.B. gehen, ging, gegangen)
)
