package com.collabnotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    selectedFilterColor: Color?,
    onColorSelected: (Color?) -> Unit,
    postItColors: List<Color>,
    modifier: Modifier = Modifier
) {
    var showColorFilterDialog by remember { mutableStateOf(false) }

    // Botão flutuante da lupa que abre o diálogo de filtro por cor
    FloatingActionButton(
        onClick = { showColorFilterDialog = true },
        containerColor = if (selectedFilterColor != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Filtrar por Cor com Lupa"
        )
    }

    // Diálogo de seleção de cores ativado pela lupa
    if (showColorFilterDialog) {
        AlertDialog(
            onDismissRequest = { showColorFilterDialog = false },
            title = { Text("Filtrar por Cor") },
            text = {
                Column {
                    Text("Seleciona uma cor para ver os post-its correspondentes:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        postItColors.take(5).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedFilterColor == color) 3.dp else 1.dp,
                                        color = if (selectedFilterColor == color) Color.Black else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onColorSelected(color)
                                        showColorFilterDialog = false
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        postItColors.drop(5).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedFilterColor == color) 3.dp else 1.dp,
                                        color = if (selectedFilterColor == color) Color.Black else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onColorSelected(color)
                                        showColorFilterDialog = false
                                    }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onColorSelected(null) // Limpa o filtro
                    showColorFilterDialog = false
                }) {
                    Text("Mostrar Todas")
                }
            },
            dismissButton = {
                TextButton(onClick = { showColorFilterDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}