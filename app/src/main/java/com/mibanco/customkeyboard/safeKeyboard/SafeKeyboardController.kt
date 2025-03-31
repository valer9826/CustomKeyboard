package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp

@ExperimentalFoundationApi
class SafeKeyboardController(
    val focusManager: FocusManager
) {
    var isKeyboardVisible by mutableStateOf(false)
        private set

    var keyboardHeightPx by mutableIntStateOf(0)
    val keyboardHeightDp: Dp
        @Composable get() = with(LocalDensity.current) { keyboardHeightPx.toDp() }

    var keyboardType by mutableStateOf(KeyboardType.Number)
        private set

    private val currentFocusRequester = mutableStateOf<BringIntoViewRequester?>(null)

    fun toggleKeyboardType() {
        keyboardType = if (keyboardType == KeyboardType.Number) KeyboardType.Text else KeyboardType.Number
    }

    fun openKeyboard(requester: BringIntoViewRequester) {
        currentFocusRequester.value = requester
        isKeyboardVisible = true
    }

    fun hideKeyboard() {
        isKeyboardVisible = false
        focusManager.clearFocus(force = true)
        currentFocusRequester.value = null
    }

    fun getCurrentFocusRequester(): BringIntoViewRequester? = currentFocusRequester.value
}
