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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SafeKeyboardLayout(
    keyboardType: KeyboardType,
    focusManager: FocusManager,
    keyboardPositionMode: KeyboardPositionMode = KeyboardPositionMode.FOLLOW_FOCUSED_FIELD,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    content: @Composable (
        onOpenKeyboard: () -> Unit,
        onKeyboardDismiss: () -> Unit,
        keyboardPositionMode: KeyboardPositionMode,
        lastItemRequester: BringIntoViewRequester
    ) -> Unit
) {
    val scrollState = rememberScrollState()
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val keyboardHeightPx = remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val keyboardHeightDp = with(density) { keyboardHeightPx.intValue.toDp() }
    val lastItemRequester = remember { BringIntoViewRequester() }

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
                    { isKeyboardVisible = true },
                    { isKeyboardVisible = false },
                    keyboardPositionMode,
                    lastItemRequester
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
                        onKeyPress = onKeyPress,
                        onDelete = onDelete
                    )
                }
            }
        }
    }

    LaunchedEffect(isKeyboardVisible, keyboardPositionMode) {
        if (
            isKeyboardVisible &&
            keyboardPositionMode == KeyboardPositionMode.FIXED_TO_BOTTOM_OF_CONTENT
        ) {
            lastItemRequester.bringIntoView()
        }
    }
}

