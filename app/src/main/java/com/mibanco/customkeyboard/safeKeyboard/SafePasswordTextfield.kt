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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SafePasswordTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyboard: () -> Unit,
    onKeyboardDismiss: () -> Unit,
    bringIntoViewRequester: BringIntoViewRequester,
    keyboardPositionMode: KeyboardPositionMode,
    fieldIndex: Int,
    focusedFieldIndex: MutableIntState,
    allowCursorPlacement: Boolean = false
) {
    var hasFocus by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isCursorVisible by remember { mutableStateOf(true) }
    var lastInputTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val cursorIndex = rememberCursorIndex(
        value = value,
        hasFocus = hasFocus
    )

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

    LaunchedEffect(hasFocus, keyboardPositionMode) {
        if (hasFocus && keyboardPositionMode == KeyboardPositionMode.FOLLOW_FOCUSED_FIELD) {
            delay(300)
            bringIntoViewRequester.bringIntoView()
        }
    }

    TextField(
        value = value,
        onValueChange = {
            val adjusted = if (!allowCursorPlacement) {
                it.copy(selection = TextRange(it.text.length))
            } else {
                it
            }
            onValueChange(adjusted)
        },
        label = { Text("Ingrese valor") },
        readOnly = true,
        visualTransformation = MaskVisualTransformation(
            isPasswordVisible = isPasswordVisible,
            isCursorVisible = isCursorVisible,
            cursorIndex = cursorIndex,
            originalText = value.text
        ),
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

                if (focusState.isFocused) {
                    focusedFieldIndex.intValue = fieldIndex
                    onOpenKeyboard()
                } else {
                    if (focusedFieldIndex.intValue == fieldIndex) {
                        focusedFieldIndex.intValue = -1
                        onKeyboardDismiss()
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

@Composable
fun rememberCursorIndex(
    value: TextFieldValue,
    hasFocus: Boolean
): Int {
    return if (hasFocus) {
        value.selection.start.coerceIn(0, value.text.length)
    } else -1
}