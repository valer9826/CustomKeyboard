package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SafePasswordTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyboard: () -> Unit,
    onKeyboardDismiss: () -> Unit,
    isKeyboardVisible: Boolean,
    bringIntoViewRequester: BringIntoViewRequester,
    coroutineScope: CoroutineScope,
    keyboardPositionMode: KeyboardPositionMode
) {
    var hasFocus by remember { mutableStateOf(false) }
    var cursorManuallyMoved by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isCursorVisible by remember { mutableStateOf(true) }
    var lastInputTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(value.text) {
        lastInputTime = System.currentTimeMillis()
        isCursorVisible = true
    }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            while (true) {
                delay(500)
                val now = System.currentTimeMillis()
                isCursorVisible = if (now - lastInputTime > 500) {
                    !isCursorVisible
                } else {
                    true
                }
            }
        }
    }

    val cursorIndex = if (hasFocus) {
        value.selection.start.coerceIn(0, value.text.length)
    } else -1

    if (hasFocus && !cursorManuallyMoved && value.selection.start != value.text.length) {
        onValueChange(value.copy(selection = TextRange(value.text.length)))
    }

    val transformedText = remember(value.text, cursorIndex, isPasswordVisible, isCursorVisible) {
        val builder = buildString {
            value.text.forEachIndexed { index, char ->
                if (index == cursorIndex && isCursorVisible) append('|')
                append(if (isPasswordVisible) char else '•')
            }
            if (cursorIndex == value.text.length && isCursorVisible) append('|')
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + if (cursorIndex in 0..<offset && isCursorVisible) 1 else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    cursorIndex < 0 -> offset.coerceAtMost(value.text.length)
                    offset <= cursorIndex -> offset
                    offset == cursorIndex + 1 && isCursorVisible -> cursorIndex
                    else -> value.text.length
                }
            }
        }

        TransformedText(AnnotatedString(builder), offsetMapping)
    }

    TextField(
        value = value,
        onValueChange = {
            if (it.selection.start != value.selection.start) {
                cursorManuallyMoved = true
            }
            onValueChange(it)
        },
        label = { Text("Ingrese valor") },
        readOnly = true,
        visualTransformation = VisualTransformation { transformedText },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = modifier
            .then(
                if (keyboardPositionMode == KeyboardPositionMode.FOLLOW_FOCUSED_FIELD) {
                    Modifier.bringIntoViewRequester(bringIntoViewRequester)
                } else {
                    Modifier
                }
            )
            .onFocusChanged { focusState ->
                hasFocus = focusState.isFocused
                if (!focusState.isFocused) {
                    cursorManuallyMoved = false
                    onKeyboardDismiss()
                }
                if (focusState.isFocused && !isKeyboardVisible) {
                    onOpenKeyboard()
                    if (keyboardPositionMode == KeyboardPositionMode.FOLLOW_FOCUSED_FIELD) {
                        coroutineScope.launch {
                            delay(300)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
            },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    )
}
