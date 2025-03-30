package com.mibanco.customkeyboard.safeKeyboard

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SafeInputScreen() {
    val focusManager = LocalFocusManager.current
    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var lastItemRequester = remember { BringIntoViewRequester() }
    val buttonComposed = remember { mutableStateOf(false) }
    var keyboardPositionMode = remember { KeyboardPositionMode.FOLLOW_FOCUSED_FIELD }

//    EnableSecureFlag()

    SafeKeyboardLayout(
        keyboardType = keyboardType,
        isKeyboardVisible = isKeyboardVisible,
        onKeyboardVisibilityChanged = { isKeyboardVisible = it },
        focusManager = focusManager
    ) { onOpenKeyboard, password, setPassword, listState ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            item {
                Text("Ingresa tu valor:", fontSize = 20.sp)
                Spacer(Modifier.height(12.dp))
            }

            items(10) { index ->
                val inputRequester = remember { BringIntoViewRequester() }

                SafePasswordTextField(
                    value = password,
                    onValueChange = setPassword,
                    modifier = Modifier
                        .fillMaxWidth(),
                    isKeyboardVisible = isKeyboardVisible,
                    bringIntoViewRequester = inputRequester,
                    keyboardPositionMode = keyboardPositionMode,
                    coroutineScope = coroutineScope,
                    onOpenKeyboard = {
                        onOpenKeyboard()
                        isKeyboardVisible = true
                    },
                    onKeyboardDismiss = { isKeyboardVisible = false }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                key("lastItem") {

                    Button(
                        modifier = Modifier
                            .bringIntoViewRequester(lastItemRequester)
                            .onGloballyPositioned {
                                buttonComposed.value = true
                            },
                        onClick = {
                            keyboardType =
                                if (keyboardType == KeyboardType.Number) KeyboardType.Text else KeyboardType.Number
                            focusManager.clearFocus(force = true)
                        }
                    ) {
                        Text("Cambiar Teclado")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        LaunchedEffect(isKeyboardVisible, buttonComposed.value) {
            if (
                isKeyboardVisible &&
                keyboardPositionMode == KeyboardPositionMode.FIXED_TO_BOTTOM_OF_CONTENT &&
                buttonComposed.value
            ) {
                delay(50)
                coroutineScope.launch {
                    listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
                }
            }
        }
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

