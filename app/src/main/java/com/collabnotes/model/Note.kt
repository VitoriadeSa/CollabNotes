package com.collabnotes.model

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val authorEmail: String = "",
    val colorHex: Long = 0xFFFFF9C4
) {
    val userHandle: String
        get() = "@${authorEmail.substringBefore("@")}"
}