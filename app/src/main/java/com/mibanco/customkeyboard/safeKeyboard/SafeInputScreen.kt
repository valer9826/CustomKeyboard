package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SafeInputScreen() {
    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var keyboardVisible by remember { mutableStateOf(false) }

    SafeKeyboardScaffold(
        keyboardType = keyboardType,) { onOpenKeyboard, password, setPassword, isKeyboardVisible  ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ingresa tu valor:", fontSize = 20.sp)

            SafePasswordTextfield(
                value = password,
                onValueChange = { setPassword(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.hide()
                            onOpenKeyboard()
                        }
                    },
                keyboardVisible = keyboardVisible,
                onOpenKeyboard = {
                    keyboardVisible = true
                    onOpenKeyboard()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                keyboardType =
                    if (keyboardType == KeyboardType.Number) KeyboardType.Text else KeyboardType.Number
                focusManager.clearFocus(force = true)
            }) {
                Text("Cambiar Teclado")
            }
        }
    }
}

