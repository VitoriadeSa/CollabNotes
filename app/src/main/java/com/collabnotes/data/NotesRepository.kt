package com.collabnotes.data

import com.collabnotes.model.Note
import com.collabnotes.model.NoteEdit
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

/**
 * Único ponto de acesso ao Firestore.
 *
 * Estrutura:
 *   notes/{noteId}                  -> a nota (texto atual, cor, autor, última edição)
 *   notes/{noteId}/edits/{editId}   -> histórico: uma entrada por criação/edição
 */
class NotesRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val notes by lazy { db.collection("notes") }

    // READ em tempo real das notas
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

    // READ em tempo real do histórico de uma nota (mais recente primeiro)
    fun listenToEdits(
        noteId: String,
        onChange: (List<NoteEdit>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration =
        notes.document(noteId).collection("edits")
            .orderBy("editedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    onChange(snapshot.documents.mapNotNull { it.toObject(NoteEdit::class.java) })
                }
            }

    // CREATE: a nota + a primeira entrada do histórico, na mesma operação atómica
    fun addNote(note: Note): Task<Void> {
        val noteRef = notes.document(note.id)
        val editRef = noteRef.collection("edits").document()
        return db.runBatch { batch ->
            batch.set(noteRef, note)
            batch.set(
                editRef,
                NoteEdit(
                    id = editRef.id,
                    type = NoteEdit.TYPE_CREATED,
                    editorEmail = note.authorEmail,
                    editedAt = note.createdAt,
                    content = note.content
                )
            )
        }
    }

    // UPDATE: qualquer utilizador autenticado pode editar; fica registado quem e quando
    fun updateNote(id: String, content: String, colorHex: Long, editorEmail: String): Task<Void> {
        val now = System.currentTimeMillis()
        val noteRef = notes.document(id)
        val editRef = noteRef.collection("edits").document()
        return db.runBatch { batch ->
            batch.update(
                noteRef,
                mapOf(
                    "content" to content,
                    "colorHex" to colorHex,
                    "lastEditedBy" to editorEmail,
                    "lastEditedAt" to now
                )
            )
            batch.set(
                editRef,
                NoteEdit(
                    id = editRef.id,
                    type = NoteEdit.TYPE_EDITED,
                    editorEmail = editorEmail,
                    editedAt = now,
                    content = content
                )
            )
        }
    }

    // DELETE: apaga a nota e também o histórico (o Firestore não apaga subcoleções sozinho)
    fun deleteNote(id: String): Task<Void> {
        val noteRef = notes.document(id)
        return noteRef.collection("edits").get().continueWithTask { task ->
            val editDocs = if (task.isSuccessful) task.result?.documents.orEmpty() else emptyList()
            db.runBatch { batch ->
                editDocs.forEach { batch.delete(it.reference) }
                batch.delete(noteRef)
            }
        }
    }
}
