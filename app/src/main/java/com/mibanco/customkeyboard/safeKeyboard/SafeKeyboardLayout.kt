package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeKeyboardLayout(
    keyboardType: KeyboardType,
    isKeyboardVisible: Boolean,
    onKeyboardVisibilityChanged: (Boolean) -> Unit,
    content: @Composable (
        onOpenKeyboard: () -> Unit,
        password: TextFieldValue,
        setPassword: (TextFieldValue) -> Unit
    ) -> Unit
) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isUpperCase by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val keyboardHeightPx = remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val keyboardHeightDp = with(density) { keyboardHeightPx.value.toDp() }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = if (isKeyboardVisible) keyboardHeightDp else 0.dp)
        ) {
            // Esto permite que el contenido se alinee en el bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter), // Aquí obligas que inicie desde abajo
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content(
                    { onKeyboardVisibilityChanged(true) },
                    inputText,
                    { inputText = it }
                )
            }
        }

        AnimatedVisibility(
            visible = isKeyboardVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .onGloballyPositioned { layoutCoordinates ->
                        keyboardHeightPx.value = layoutCoordinates.size.height
                    }
            ) {
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
                )
            }
        }
    }
}




