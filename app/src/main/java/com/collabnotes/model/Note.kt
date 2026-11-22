package com.collabnotes.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.Exclude
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val authorEmail: String = "",
    val colorHex: Long = 0xFFFFF9C4,
    val createdAt: Long = System.currentTimeMillis(),
    // Última edição (lastEditedAt == 0 significa "nunca foi editada")
    val lastEditedBy: String = "",
    val lastEditedAt: Long = 0L
) {
    @get:Exclude
    val userHandle: String
        get() = "@${authorEmail.substringBefore("@")}"

    @get:Exclude
    val lastEditorHandle: String
        get() = "@${lastEditedBy.substringBefore("@")}"
}

// Converte uma Color do Compose para o valor ARGB de 32 bits guardado em colorHex
fun Color.toColorHex(): Long = toArgb().toLong() and 0xFFFFFFFFL
