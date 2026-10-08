package com.collabnotes.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.collabnotes.model.Note
import com.collabnotes.ui.components.NoteCard
import com.collabnotes.ui.components.SearchBar
import com.google.firebase.auth.FirebaseAuth

val postItColors = listOf(
    Color(0xFFFFF9C4), // Amarelo clássico
    Color(0xFFC8E6C9), // Verde claro
    Color(0xFFB3E5FC), // Azul claro
    Color(0xFFFFCCBC), // Laranja claro
    Color(0xFFE1BEE7), // Roxo claro
    Color(0xFFF8BBD0), // Rosa claro
    Color(0xFFD7CCC8), // Castanho claro
    Color(0xFFCFD8DC), // Cinzento claro
    Color(0xFFFFF59D), // Amarelo vivo
    Color(0xFFB9F6CA)  // Verde menta
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    auth: FirebaseAuth? = null,
    notesState: MutableState<List<Note>> = remember { mutableStateOf(listOf()) },
    onLogout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    currentUserEmail: String = auth?.currentUser?.email ?: "utilizador@collabnotes.com"
) {
    val context = LocalContext.current
    val userHandle = currentUserEmail.substringBefore("@")

    // Estados
    var selectedFilterColor by remember { mutableStateOf<Color?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newNoteText by remember { mutableStateOf("") }
    var selectedColorForNew by remember { mutableStateOf(postItColors[0]) }

    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var editedNoteText by remember { mutableStateOf("") }
    var selectedColorForEdit by remember { mutableStateOf(postItColors[0]) }

    // Filtragem de notas pela cor selecionada na lupa
    val filteredNotes = if (selectedFilterColor == null) {
        notesState.value
    } else {
        notesState.value.filter { it.colorHex == selectedFilterColor!!.value.toLong() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Título exato: CollabNotes com o "Notes" a roxo
                    Text(
                        buildAnnotatedString {
                            append("Collab")
                            withStyle(style = SpanStyle(color = Color(0xFF6200EE), fontWeight = FontWeight.Bold)) {
                                append("Notes")
                            }
                        },
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Definições")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Terminar Sessão")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botão de Adicionar (+)
                FloatingActionButton(onClick = {
                    newNoteText = ""
                    selectedColorForNew = postItColors[0]
                    showAddDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Nota")
                }

                // Componente SearchBar (botão da lupa embaixo do + para filtrar por cor)
                SearchBar(
                    selectedFilterColor = selectedFilterColor,
                    onColorSelected = { selectedFilterColor = it },
                    postItColors = postItColors
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Saudação "Bem-vindo, @utilizador"
            Text(
                text = "Bem-vindo, @$userHandle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            // Indicador visual de filtro de cor ativo
            if (selectedFilterColor != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("A filtrar por cor", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { selectedFilterColor = null }) {
                        Text("Limpar filtro", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Nenhuma nota encontrada", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredNotes) { note ->
                        NoteCard(
                            note = note,
                            currentUserEmail = currentUserEmail,
                            onEditClick = {
                                noteToEdit = note
                                editedNoteText = note.content
                                selectedColorForEdit = Color(note.colorHex)
                            },
                            onDeleteClick = {
                                notesState.value = notesState.value.filter { it.id != note.id }
                                Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Diálogo para Adicionar Nova Nota
        if (showAddDialog) {
            NoteDialog(
                title = "Nova Nota",
                textValue = newNoteText,
                onTextChange = { newNoteText = it },
                selectedColor = selectedColorForNew,
                onColorSelected = { selectedColorForNew = it },
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    if (newNoteText.isNotBlank()) {
                        val note = Note(
                            content = newNoteText,
                            authorEmail = currentUserEmail,
                            colorHex = selectedColorForNew.value.toLong()
                        )
                        notesState.value = listOf(note) + notesState.value
                        showAddDialog = false
                        Toast.makeText(context, "Nota criada!", Toast.LENGTH_SHORT).show()
                    }
                },
                confirmButtonText = "Criar"
            )
        }

        // Diálogo para Editar Nota
        noteToEdit?.let { currentNote ->
            NoteDialog(
                title = "Editar Nota",
                textValue = editedNoteText,
                onTextChange = { editedNoteText = it },
                selectedColor = selectedColorForEdit,
                onColorSelected = { selectedColorForEdit = it },
                onDismiss = { noteToEdit = null },
                onConfirm = {
                    if (editedNoteText.isNotBlank()) {
                        notesState.value = notesState.value.map {
                            if (it.id == currentNote.id) {
                                it.copy(
                                    content = editedNoteText,
                                    colorHex = selectedColorForEdit.value.toLong()
                                )
                            } else {
                                it
                            }
                        }
                        noteToEdit = null
                        Toast.makeText(context, "Nota atualizada!", Toast.LENGTH_SHORT).show()
                    }
                },
                confirmButtonText = "Guardar"
            )
        }
    }
}