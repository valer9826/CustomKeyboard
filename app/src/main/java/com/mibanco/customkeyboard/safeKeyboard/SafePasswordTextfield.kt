package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SafePasswordTextfield(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyboard: () -> Unit,
    keyboardVisible: Boolean
) {
    var hasFocus by remember { mutableStateOf(false) }
    var cursorManuallyMoved by remember { mutableStateOf(false) }

    val cursorIndex = if (hasFocus) {
        value.selection.start.coerceIn(0, value.text.length)
    } else -1

    // Si el TextField se enfoca y el cursor no fue movido manualmente, lo llevamos al final
    if (hasFocus && !cursorManuallyMoved && value.selection.start != value.text.length) {
        onValueChange(
            value.copy(selection = TextRange(value.text.length))
        )
    }

    val transformedText = remember(value.text, cursorIndex) {
        val maskedText = buildString {
            value.text.forEachIndexed { index, _ ->
                if (index == cursorIndex) append('|')
                append('•')
            }
            if (cursorIndex == value.text.length) append('|')
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + if (cursorIndex in 0..<offset) 1 else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    cursorIndex < 0 -> offset.coerceAtMost(value.text.length)
                    offset <= cursorIndex -> offset
                    offset == cursorIndex + 1 -> cursorIndex
                    else -> value.text.length
                }
            }
        }

        TransformedText(AnnotatedString(maskedText), offsetMapping)
    }

    TextField(
        value = value,
        onValueChange = {
            // Detecta si el usuario cambió el cursor a otro lugar (manual)
            if (it.selection.start != value.selection.start) {
                cursorManuallyMoved = true
            }
            onValueChange(it)
        },
        label = { Text("Ingrese valor") },
        readOnly = true,
        visualTransformation = VisualTransformation { transformedText },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = modifier.onFocusChanged { focusState ->
            hasFocus = focusState.isFocused

            if (!focusState.isFocused) {
                cursorManuallyMoved = false
            }

            if (focusState.isFocused && !keyboardVisible) {
                onOpenKeyboard()
            }
        }
    )
}

