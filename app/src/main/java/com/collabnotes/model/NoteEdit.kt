
package com.collabnotes.model

data class NoteEdit(
    val id: String = "",
    val type: String = TYPE_CREATED,
    val editorEmail: String = "",
    val editedAt: Long = 0L,
    val content: String = ""
) {
    companion object {
        const val TYPE_CREATED = "CREATED"
        const val TYPE_EDITED = "EDITED"
    }
}
