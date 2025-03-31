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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable (
        onOpenKeyboard: () -> Unit,
        passwords: List<TextFieldValue>,
        setPassword: (Int, TextFieldValue) -> Unit,
        focusedFieldIndex: MutableIntState
    ) -> Unit
) {
    val passwords = remember {
        mutableStateListOf<TextFieldValue>().apply {
            repeat(TEST_NUMBER) { add(TextFieldValue()) }
        }
    }
    val scrollState = rememberScrollState()
    val focusedFieldIndex = remember { mutableIntStateOf(-1) }
    val keyboardHeightPx = remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val keyboardHeightDp = with(density) { keyboardHeightPx.intValue.toDp() }

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
                    .verticalScroll(scrollState)
                    .padding(bottom = if (isKeyboardVisible) keyboardHeightDp + 8.dp else 0.dp),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment
            ) {
                content(
                    { onKeyboardVisibilityChanged(true) },
                    passwords,
                    { index, value -> passwords[index] = value },
                    focusedFieldIndex
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
                            val index = focusedFieldIndex.intValue
                            if (index in passwords.indices) {
                                val old = passwords[index]
                                val cursor = old.selection.start
                                val newText =
                                    old.text.substring(0, cursor) + key + old.text.substring(cursor)
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
                        }
                    )
                }
            }
        }
    }
}