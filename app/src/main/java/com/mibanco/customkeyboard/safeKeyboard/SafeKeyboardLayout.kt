package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SafeKeyboardLayout(
    keyboardType: KeyboardType,
    isKeyboardVisible: Boolean,
    onKeyboardVisibilityChanged: (Boolean) -> Unit,
    focusManager: FocusManager,
    content: @Composable (
        onOpenKeyboard: () -> Unit,
        password: TextFieldValue,
        setPassword: (TextFieldValue) -> Unit,
        listState: LazyListState
    ) -> Unit
) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val keyboardHeightPx = remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val keyboardHeightDp = with(density) { keyboardHeightPx.intValue.toDp() }

    val listState = rememberLazyListState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus(force = true) }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (isKeyboardVisible) keyboardHeightDp + 8.dp else 0.dp)
            ) {
                content(
                    { onKeyboardVisibilityChanged(true) },
                    inputText,
                    { inputText = it },
                    listState
                )
            }

            AnimatedVisibility(
                visible = isKeyboardVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .onGloballyPositioned {
                            keyboardHeightPx.intValue = it.size.height
                        }
                ) {
                    SafeKeyboard(
                        type = keyboardType,
                        onKeyPress = { key ->
                            val index = inputText.selection.start
                            val newText =
                                inputText.text.substring(0, index) + key + inputText.text.substring(
                                    index
                                )
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
                    )
                }
            }
        }
    }
}