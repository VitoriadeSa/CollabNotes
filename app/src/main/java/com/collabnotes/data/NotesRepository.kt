package com.collabnotes.data

import com.collabnotes.model.Note
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

/**
 * Único ponto de acesso ao Firestore. A coleção "notes" é partilhada por todos os
 * utilizadores autenticados (é isso que torna a app colaborativa); quem pode
 * editar/apagar cada nota é decidido pelas Security Rules (ver firestore.rules).
 */
class NotesRepository {

    // lazy: só toca no Firestore quando for realmente usado (evita crashes em Previews)
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val notes by lazy { db.collection("notes") }

    /**
     * READ em tempo real. O callback é chamado:
     *  - uma vez com o estado atual (da cache local ou do servidor),
     *  - de novo sempre que qualquer nota é criada/alterada/apagada, por qualquer utilizador,
     *  - também imediatamente após uma escrita local, mesmo offline.
     * O Boolean indica se os dados vêm da cache local (offline ou ainda a sincronizar).
     */
    fun listenToNotes(
        onChange: (notes: List<Note>, fromCache: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration =
        notes
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
                    onChange(list, snapshot.metadata.isFromCache)
                }
            }

    // CREATE
    fun addNote(note: Note): Task<Void> =
        notes.document(note.id).set(note)

    // UPDATE (só os campos editáveis; autor e data de criação não mudam)
    fun updateNote(id: String, content: String, colorHex: Long): Task<Void> =
        notes.document(id).update(
            mapOf(
                "content" to content,
                "colorHex" to colorHex
            )
        )

    // DELETE
    fun deleteNote(id: String): Task<Void> =
        notes.document(id).delete()
}
