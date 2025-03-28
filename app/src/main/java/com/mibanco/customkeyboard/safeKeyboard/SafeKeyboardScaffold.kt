package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeKeyboardScaffold(
    keyboardType: KeyboardType,
    content: @Composable (
        onOpenKeyboard: () -> Unit,
        password: TextFieldValue,
        setPassword: (TextFieldValue) -> Unit
    ) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isUpperCase by remember { mutableStateOf(false) }
    var isRequestingShow by remember { mutableStateOf(false) }
    var density = LocalDensity.current

    val sheetState = remember(density) {
        SheetState(
            skipHiddenState = false,
            skipPartiallyExpanded = true,
            density = density,
            initialValue = SheetValue.Hidden,
            confirmValueChange = { newValue ->
                // Evita que se oculte justo después de mostrar
                newValue != SheetValue.Hidden || !isRequestingShow
            }
        )
    }

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    val isKeyboardVisible = sheetState.currentValue == SheetValue.Expanded

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus(force = true)
            isRequestingShow = false
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = Color.White, // o el color de fondo deseado
        sheetShape = RectangleShape,       // 🔥 Quita los bordes redondeados
        sheetDragHandle = {},
        sheetContent = {
            SafeKeyboard(
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
                },
                onEnter = {
                    coroutineScope.launch {
                        isRequestingShow = false
                        sheetState.hide()
                    }
                },
                onShiftToggle = { isUpperCase = !isUpperCase },
                isUpperCase = isUpperCase
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        // Si el usuario toca fuera del TextField
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.hide()
                            }
                            focusManager.clearFocus(force = true)
                        }
                    }
                },
            verticalArrangement = Arrangement.Center
        ) {
            content(
                {
                    coroutineScope.launch {
                        isRequestingShow = true
                        sheetState.expand()
                    }
                },
                 inputText,
                { inputText = it }
            )
        }
    }
}


