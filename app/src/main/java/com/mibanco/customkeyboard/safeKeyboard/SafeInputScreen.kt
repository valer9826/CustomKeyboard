package com.mibanco.customkeyboard.safeKeyboard

import android.view.WindowManager
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SafeInputScreen() {
//    EnableSecureFlag()

    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isKeyboardVisible by remember { mutableStateOf(false) }

    SafeKeyboardScaffold(keyboardType = keyboardType) { onOpenKeyboard, password, setPassword ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ingresa tu valor:", fontSize = 20.sp)

            SafePasswordTextField(
                value = password,
                onValueChange = { setPassword(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onOpenKeyboard()
                        }
                    },
                isKeyboardVisible = isKeyboardVisible,
                onOpenKeyboard = {
                    isKeyboardVisible = true
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

@Composable
fun EnableSecureFlag() {
    val context = LocalContext.current
    val view = LocalView.current

    SideEffect {
        val activity = context as? ComponentActivity
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}

