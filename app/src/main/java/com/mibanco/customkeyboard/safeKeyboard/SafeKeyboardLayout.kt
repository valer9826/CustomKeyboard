package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeKeyboardLayout(
    controller: SafeKeyboardController,
    content: @Composable (
        password: TextFieldValue,
        setPassword: (TextFieldValue) -> Unit,
        listState: LazyListState
    ) -> Unit
) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        controller.hideKeyboard()
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (controller.isKeyboardVisible) controller.keyboardHeightDp + 8.dp else 0.dp)
            ) {
                content(inputText, { inputText = it }, listState)
            }

            AnimatedVisibility(
                visible = controller.isKeyboardVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .onGloballyPositioned {
                            controller.keyboardHeightPx = it.size.height
                        }
                ) {
                    SafeKeyboard(
                        type = controller.keyboardType,
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
                        }
                    )
                }
            }
        }
    }
}
