package com.collabnotes.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.Exclude
import java.util.UUID

// Todos os campos têm valor por omissão -> o Kotlin gera um construtor sem argumentos,
// que o Firestore precisa para converter documentos em Note (toObject).
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val authorEmail: String = "",
    val colorHex: Long = 0xFFFFF9C4,
    val createdAt: Long = System.currentTimeMillis()
) {
    // @get:Exclude impede que o Firestore grave esta propriedade calculada no documento
    @get:Exclude
    val userHandle: String
        get() = "@${authorEmail.substringBefore("@")}"
}

// Converte uma Color do Compose para o valor ARGB de 32 bits guardado em colorHex
fun Color.toColorHex(): Long = toArgb().toLong() and 0xFFFFFFFFL
