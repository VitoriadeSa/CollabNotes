
package com.collabnotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.collabnotes.data.NotesRepository
import com.collabnotes.model.Note
import com.collabnotes.model.NoteEdit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteHistoryDialog(
    note: Note,
    repository: NotesRepository,
    onDismiss: () -> Unit
) {
    val edits = remember(note.id) {
        mutableStateListOf<NoteEdit>()
    }

    var isLoading by remember(note.id) {
        mutableStateOf(true)
    }

    var error by remember(note.id) {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(note.id) {
        val registration = repository.listenToEdits(
            noteId = note.id,
            onChange = { list ->
                edits.clear()
                edits.addAll(list)
                isLoading = false
            },
            onError = { e ->
                error = e.message
                isLoading = false
            }
        )

        onDispose {
            registration.remove()
        }
    }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Histórico da nota")
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                when {
                    isLoading -> Text("A carregar histórico...")

                    error != null -> Text("Erro: $error")

                    edits.isEmpty() -> Text("Sem histórico disponível.")

                    else -> LazyColumn {
                        items(edits, key = { it.id }) { edit ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        if (edit.type == NoteEdit.TYPE_CREATED)
                                            "Nota criada"
                                        else
                                            "Nota editada",
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    Text("Por: ${edit.editorEmail}")

                                    Text(
                                        if (edit.editedAt > 0)
                                            dateFormat.format(Date(edit.editedAt))
                                        else
                                            "Data desconhecida"
                                    )

                                    Text("Conteúdo: ${edit.content}")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
