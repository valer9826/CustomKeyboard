package com.mibanco.customkeyboard.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeKeyboardScaffold(
    keyboardType: KeyboardType,
    content: @Composable (onOpenKeyboard: () -> Unit, text: TextFieldValue, setText: (TextFieldValue) -> Unit) -> Unit
) {
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isUpperCase by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                content(
                    { isKeyboardVisible = true },
                    inputText,
                    { inputText = it }
                )
            }
        }
    )

    if (isKeyboardVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                isKeyboardVisible = false
                focusManager.clearFocus(force = true)
            }
        ) {
            SafeKeyboard(
                type = keyboardType,
                onKeyPress = { key ->
                    val index = inputText.selection.start
                    val newText =
                        inputText.text.substring(0, index) + key + inputText.text.substring(index)
                    inputText = TextFieldValue(
                        text = newText,
                        selection = TextRange(index + key.length)
                    )
                },
                onDelete = {
                    val index = inputText.selection.start
                    if (index > 0) {
                        val newText = inputText.text.removeRange(index - 1, index)
                        inputText = TextFieldValue(
                            text = newText,
                            selection = TextRange(index - 1)
                        )
                    }
                },
                onEnter = {
                    isKeyboardVisible = false
                    focusManager.clearFocus(force = true)
                },
                onShiftToggle = { isUpperCase = !isUpperCase },
                isUpperCase = isUpperCase
            )
        }
    }
}