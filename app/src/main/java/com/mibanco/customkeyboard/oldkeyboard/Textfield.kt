package com.mibanco.customkeyboard.oldkeyboard

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun TextFieldWithKeyboard(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    onOpenKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenKeyboard() } // ✅ Abre el BottomSheet al tocar
                .clearFocusOnTouch(), // ✅ Evita que se abra el teclado nativo
            readOnly = true, // ✅ Bloqueamos la escritura manual
            label = { Text("Ingrese valor") }
        )
    }
}

// ✅ Extensión para limpiar el foco y evitar el teclado nativo
fun Modifier.clearFocusOnTouch(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    clickable { focusManager.clearFocus() }
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomKeyboardBottomSheet(
    keyboardType: KeyboardType,
    onDismiss: () -> Unit,
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onShiftToggle: () -> Unit,
    isUpperCase: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        when (keyboardType) {
            KeyboardType.Number -> RandomNumericKeyboard(onKeyPress)
            KeyboardType.Text -> AlphanumericKeyboard(
                onKeyPress = onKeyPress,
                onDelete = onDelete,
                onEnter = onEnter,
                onShiftToggle = onShiftToggle,
                isUpperCase = isUpperCase
            )
        }
    }
}



@Composable
fun NoKeyboardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    val view = LocalView.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChange(isFocused)
            },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default,
        keyboardActions = KeyboardActions.Default,
        // Clave: impedir la creación del input connection del sistema
        interactionSource = remember {
            object : MutableInteractionSource {
                override suspend fun emit(interaction: Interaction) {
                }

                override fun tryEmit(interaction: Interaction): Boolean = true
                override val interactions = MutableSharedFlow<Interaction>()
            }
        }
    )

    // Hack: bloquear el teclado del sistema al recibir foco
    DisposableEffect(isFocused) {
        if (isFocused) {
            view.hideKeyboard()
        }
        onDispose {}
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureInputScreen() {
    var input by remember { mutableStateOf("") }
    var keyboardType by remember { mutableStateOf(KeyboardType.Number) }
    var isKeyboardVisible by remember { mutableStateOf(false) } // ❌ NO SE ABRE AL INICIO
    var isUpperCase by remember { mutableStateOf(true) }

    // Estado del BottomSheet
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

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
                Text(text = "Ingresa tu valor:", fontSize = 20.sp)

                // ✅ El teclado solo se abre cuando tocamos el TextField
                TextFieldWithKeyboard(
                    value = input,
                    onValueChange = { input = it },
                    keyboardType = keyboardType,
                    onOpenKeyboard = {
                        coroutineScope.launch { bottomSheetState.show() }
                        isKeyboardVisible = true
                    }
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
    )

    // ✅ Ahora el BottomSheet solo se muestra cuando `isKeyboardVisible` es `true`
    if (isKeyboardVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { bottomSheetState.hide() }
                isKeyboardVisible = false // 🔥 SE CIERRA AL TOCAR FUERA
            },
            sheetState = bottomSheetState
        ) {
            when (keyboardType) {
                KeyboardType.Number -> RandomNumericKeyboard { key ->
                    input += key
                }
                KeyboardType.Text -> AlphanumericKeyboard(
                    onKeyPress = { key -> input += if (isUpperCase) key.uppercase() else key.lowercase() },
                    onDelete = {
                        if (input.isNotEmpty()) input = input.dropLast(1)
                    },
                    onEnter = {
                        coroutineScope.launch { bottomSheetState.hide() }
                        isKeyboardVisible = false // 🔥 SE CIERRA AL PRESIONAR "ENTER"
                    },
                    onShiftToggle = { isUpperCase = !isUpperCase },
                    isUpperCase = isUpperCase
                )
            }
        }
    }
}


