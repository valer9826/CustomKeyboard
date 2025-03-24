package com.mibanco.customkeyboard.newkeyboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mibanco.customkeyboard.ui.theme.CustomKeyboardTheme

@Composable
fun KeyboardButton(
    text: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFE0E0E0),
    isUpperCase: Boolean = false
) {
    val displayText = when (text) {
        "espacio" -> "␣"
        else -> if (isUpperCase && text.length == 1 && text[0].isLetter()) {
            text.uppercase()
        } else {
            text
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) Color(0xFFBDBDBD) else containerColor

    Button(
        onClick = { onClick(text) },
        modifier = modifier
            .padding(2.dp)
            .height(48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Text(
            text = displayText,
            fontSize = 16.sp,
            maxLines = 1,
            color = Color.Black
        )
    }
}

@Composable
fun KeyboardRow(
    keys: List<String>,
    onKeyPress: (String) -> Unit,
    isUpperCase: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keys.forEach { key ->
            KeyboardButton(
                text = key,
                onClick = onKeyPress,
                isUpperCase = isUpperCase,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun CustomAlphaKeyboard(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onShiftToggle: (() -> Unit)?,
    isUpperCase: Boolean
) {
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ñ")
    val row3 = listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "←")
    val row4 = listOf("espacio", "✔")

    val transformKey: (String) -> Unit = { key ->
        when (key) {
            "⇧" -> onShiftToggle?.invoke()
            "←" -> onDelete()
            "✔" -> onEnter()
            "espacio" -> onKeyPress(" ")
            else -> onKeyPress(if (isUpperCase) key.uppercase() else key)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(row1, row2, row3).forEach { row ->
            KeyboardRow(keys = row, onKeyPress = transformKey, isUpperCase = isUpperCase)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            KeyboardButton(
                text = "espacio",
                onClick = transformKey,
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            KeyboardButton(
                text = "✔",
                onClick = transformKey,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun CustomNumericKeyboard(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit
) {
    val numbers = remember { (1..9).map { it.toString() }.shuffled() }

    val rows = numbers.chunked(3) + listOf(listOf(",", "0", "."))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            KeyboardRow(keys = row, onKeyPress = onKeyPress)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeyboardButton(
                text = "←",
                onClick = { onDelete() },
                modifier = Modifier.weight(1f)
            )
            KeyboardButton(
                text = "✔",
                onClick = { onEnter() },
                modifier = Modifier.weight(1f)
            )
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
    if (type == KeyboardType.Number) {
        CustomNumericKeyboard(
            onKeyPress = onKeyPress,
            onDelete = onDelete,
            onEnter = onEnter
        )
    } else {
        CustomAlphaKeyboard(
            onKeyPress = onKeyPress,
            onDelete = onDelete,
            onEnter = onEnter,
            onShiftToggle = onShiftToggle,
            isUpperCase = isUpperCase
        )
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

@Preview(showBackground = true)
@Composable
fun CustomAlphaKeyboardPreview() {
    CustomKeyboardTheme {
        CustomAlphaKeyboard(
            onKeyPress = {},
            onDelete = {},
            onEnter = {},
            onShiftToggle = {},
            isUpperCase = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomNumericKeyboardPreview() {
    CustomKeyboardTheme {
        CustomNumericKeyboard(
            onKeyPress = {},
            onDelete = {},
            onEnter = {}
        )
    }
}