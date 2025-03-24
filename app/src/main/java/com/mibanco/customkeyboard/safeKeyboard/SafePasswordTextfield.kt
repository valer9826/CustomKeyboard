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

    val isCursorVisible = hasFocus && keyboardVisible
    val cursorIndex = if (isCursorVisible) {
        value.selection.start.coerceIn(0, value.text.length)
    } else -1

    val maskedTransformation = VisualTransformation { text ->
        val maskedBuilder = buildString {
            text.text.forEachIndexed { i, _ ->
                if (i == cursorIndex) append('|')
                append('•')
            }

            // Mostrar cursor al final si está justo después del último carácter
            if (cursorIndex == text.text.length) {
                append('|')
            }
        }

        val maskedText = maskedBuilder.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + if (isCursorVisible && offset >= cursorIndex) 1 else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (!isCursorVisible) return offset.coerceAtMost(text.text.length)
                return when {
                    offset <= cursorIndex -> offset
                    offset == cursorIndex + 1 -> cursorIndex
                    else -> text.text.length
                }
            }
        }

        TransformedText(AnnotatedString(maskedText), offsetMapping)
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Ingrese valor") },
        readOnly = true,
        visualTransformation = maskedTransformation,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = modifier.onFocusChanged { focusState ->
            hasFocus = focusState.isFocused
            if (focusState.isFocused && !keyboardVisible) {
                onOpenKeyboard()
            }
        }
    )
}










