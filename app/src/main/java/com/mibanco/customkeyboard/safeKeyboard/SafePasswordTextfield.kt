package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import kotlinx.coroutines.delay

@Composable
fun SafePasswordTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyboard: () -> Unit,
    isKeyboardVisible: Boolean
) {
    var hasFocus by remember { mutableStateOf(false) }
    var cursorManuallyMoved by remember { mutableStateOf(false) }
    var cursorVisible by remember { mutableStateOf(true) }
    var lastInputTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val cursorIndex = if (hasFocus) value.selection.start.coerceIn(0, value.text.length) else -1

    if (hasFocus && !cursorManuallyMoved && value.selection.start != value.text.length) {
        onValueChange(value.copy(selection = TextRange(value.text.length)))
    }

    LaunchedEffect(value.text, value.selection) {
        lastInputTime = System.currentTimeMillis()
        cursorVisible = true
    }

    LaunchedEffect(hasFocus) {
        cursorVisible = true
        while (hasFocus) {
            val elapsed = System.currentTimeMillis() - lastInputTime
            cursorVisible = if (elapsed >= 500) {
                !cursorVisible
            } else {
                true
            }
            delay(450)
        }
    }

    val maskedTransformation = remember(value.text, cursorIndex, cursorVisible) {
        val masked = buildString {
            value.text.forEachIndexed { i, _ ->
                if (i == cursorIndex && cursorVisible) append('|')
                append('•')
            }
            if (cursorIndex == value.text.length && cursorVisible) append('|')
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + if (cursorVisible && cursorIndex in 0..<offset) 1 else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    cursorIndex < 0 -> offset.coerceAtMost(value.text.length)
                    offset <= cursorIndex -> offset
                    offset == cursorIndex + 1 && cursorVisible -> cursorIndex
                    else -> value.text.length
                }
            }
        }

        TransformedText(AnnotatedString(masked), offsetMapping)
    }

    TextField(
        value = value,
        onValueChange = {
            if (it.selection.start != value.selection.start) cursorManuallyMoved = true
            onValueChange(it)
        },
        label = { Text("Ingrese valor") },
        readOnly = true,
        visualTransformation = VisualTransformation { maskedTransformation },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = modifier.onFocusChanged { focus ->
            hasFocus = focus.isFocused
            if (!focus.isFocused) cursorManuallyMoved = false
            if (focus.isFocused && !isKeyboardVisible) onOpenKeyboard()
        }
    )
}
