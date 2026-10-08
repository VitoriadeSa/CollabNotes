package com.collabnotes.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun CollabNotesTitle(fontSize: TextUnit = 32.sp) {
    Text(
        text = buildAnnotatedString {
            append("Collab")
            withStyle(style = SpanStyle(color = Color(0xFF7C4DFF), fontWeight = FontWeight.ExtraBold)) {
                append("Notes")
            }
        },
        fontSize = fontSize,
        fontWeight = FontWeight.Bold
    )
}