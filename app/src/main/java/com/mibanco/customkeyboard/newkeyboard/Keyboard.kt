package com.mibanco.customkeyboard.newkeyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.mibanco.customkeyboard.ui.theme.CustomKeyboardTheme

@Composable
fun KeyboardButton(
    text: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(text) },
        modifier = modifier.height(50.dp).width(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, fontSize = 16.sp)
    }
}

@Composable
fun KeyboardRow(
    keys: List<String>,
    onKeyPress: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keys.forEach { key ->
            KeyboardButton(text = key, onClick = onKeyPress)
        }
    }
}

@Composable
fun CustomKeyboard(
    type: KeyboardType,
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onShiftToggle: (() -> Unit)? = null,
    isUpperCase: Boolean = false
) {
    val numbers = (0..9).map { it.toString() }.shuffled()
    val letters = ('A'..'Z').map { if (isUpperCase) it.toString() else it.lowercase() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (type == KeyboardType.Number) {
            KeyboardRow(keys = numbers, onKeyPress = onKeyPress)
        } else {
            letters.chunked(7).forEach { row ->
                KeyboardRow(keys = row, onKeyPress = onKeyPress)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (onShiftToggle != null) {
                    KeyboardButton("⇧", onClick = { onShiftToggle() })
                }
                KeyboardButton("←", onClick = { onDelete() })
                KeyboardButton("✔", onClick = { onEnter() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardScaffold(
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
            CustomKeyboard(
                type = keyboardType,
                onKeyPress = { key ->
                    val index = inputText.selection.start
                    val newText = inputText.text.substring(0, index) + key + inputText.text.substring(index)
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
                }
                ,
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


@Composable
fun SecureInputScreen2() {
    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }
    val keyboardController = LocalSoftwareKeyboardController.current

    KeyboardScaffold(keyboardType = keyboardType) { onOpenKeyboard, text, setText ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ingresa tu valor:", fontSize = 20.sp)

            TextField(
                value = text,
                onValueChange = { setText(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.hide()
                            onOpenKeyboard()
                        }
                    },
                readOnly = false,
                label = { Text("Ingrese valor") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                keyboardType =
                    if (keyboardType == KeyboardType.Number) KeyboardType.Text else KeyboardType.Number
            }) {
                Text("Cambiar Teclado")
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun CustomKeyboardPreview() {
    CustomKeyboardTheme {
        CustomKeyboard(
            type = KeyboardType.Text,
            onKeyPress = {},
            onEnter = {},
            onDelete = {},
            onShiftToggle = {},
        )
    }
}