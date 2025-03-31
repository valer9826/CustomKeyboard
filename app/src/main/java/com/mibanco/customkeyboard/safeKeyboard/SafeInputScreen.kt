package com.mibanco.customkeyboard.safeKeyboard

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.collections.set

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SafeInputScreen() {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }

    val passwords = remember {
        mutableStateListOf<TextFieldValue>().apply {
            repeat(TEST_NUMBER) { add(TextFieldValue()) }
        }
    }

    val focusedFieldIndex = remember { mutableIntStateOf(-1) }

    SafeKeyboardLayout(
        keyboardType = keyboardType,
        focusManager = focusManager,
        onKeyPress = { key ->
            val index = focusedFieldIndex.intValue
            if (index in passwords.indices) {
                val old = passwords[index]
                val cursor = old.selection.start
                val newText = old.text.substring(0, cursor) + key + old.text.substring(cursor)
                passwords[index] = TextFieldValue(
                    text = newText,
                    selection = TextRange(cursor + key.length)
                )
            }
        },
        onDelete = {
            val index = focusedFieldIndex.intValue
            if (index in passwords.indices) {
                val old = passwords[index]
                val cursor = old.selection.start
                if (cursor > 0) {
                    val newText = old.text.removeRange(cursor - 1, cursor)
                    passwords[index] = TextFieldValue(
                        text = newText,
                        selection = TextRange(cursor - 1)
                    )
                }
            }
        },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { onOpenKeyboard, onKeyboardDismiss, keyboardPositionMode, lastItemRequester ->

        Text("Ingresa tu valor:", fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))

        repeat(TEST_NUMBER) { index ->
            val inputRequester = remember { BringIntoViewRequester() }

            SafePasswordTextField(
                value = passwords[index],
                onValueChange = { passwords[index] = it },
                fieldIndex = index,
                focusedFieldIndex = focusedFieldIndex,
                modifier = Modifier.fillMaxWidth(),
                bringIntoViewRequester = inputRequester,
                keyboardPositionMode = keyboardPositionMode,
                coroutineScope = coroutineScope,
                onOpenKeyboard = onOpenKeyboard,
                onKeyboardDismiss = onKeyboardDismiss
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            modifier = Modifier.bringIntoViewRequester(lastItemRequester),
            onClick = {
                focusManager.clearFocus(force = true)
                keyboardType =
                    if (keyboardType == KeyboardType.Number) KeyboardType.Text else KeyboardType.Number
            }
        ) {
            Text("Cambiar Teclado")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun EnableSecureFlag() {
    val context = LocalContext.current

    SideEffect {
        val activity = context as? ComponentActivity
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}

