package com.mibanco.customkeyboard.safeKeyboard

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SafeInputScreen() {
    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isKeyboardVisible by remember { mutableStateOf(false) }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    SafeKeyboardLayout(
        keyboardType = keyboardType,
        isKeyboardVisible = isKeyboardVisible,
        onKeyboardVisibilityChanged = { isKeyboardVisible = it }
    ) { onOpenKeyboard, password, setPassword ->

        Text(text = "Ingresa tu valor:", fontSize = 20.sp)

        SafePasswordTextField(
            value = password,
            onValueChange = setPassword,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            isKeyboardVisible = isKeyboardVisible,
            bringIntoViewRequester = bringIntoViewRequester,
            coroutineScope = coroutineScope,
            onOpenKeyboard = {
                onOpenKeyboard()
                isKeyboardVisible = true
                coroutineScope.launch {
                    delay(300)
                    bringIntoViewRequester.bringIntoView()
                }
            },
            onKeyboardDismiss = {
                isKeyboardVisible = false
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            keyboardType =
                if (keyboardType == KeyboardType.Number) KeyboardType.Text else KeyboardType.Number
            focusManager.clearFocus(force = true)
        }) {
            Text("Cambiar Teclado")
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

