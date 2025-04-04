package com.mibanco.customkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mibanco.customkeyboard.safeKeyboard.SafeInputScreen
import com.mibanco.customkeyboard.ui.theme.CustomKeyboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomKeyboardTheme {
                SafeInputScreen()
            }
        }
    }
}

