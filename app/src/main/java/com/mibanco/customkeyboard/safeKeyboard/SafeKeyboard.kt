package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mibanco.customkeyboard.ui.theme.CustomKeyboardTheme

@Composable
fun KeyboardButton(
    text: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFE0E0E0),
    isUpperCase: Boolean = false
) {
    val displayText = if (isUpperCase && text?.length == 1 && text[0].isLetter()) {
        text.uppercase()
    } else {
        text
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) Color(0xFFBDBDBD) else containerColor

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(2.dp)
            .height(38.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        when {
            icon != null -> Icon(icon, contentDescription = text ?: "", tint = Color.Black)
            displayText != null -> Text(
                text = displayText,
                fontSize = 16.sp,
                maxLines = 1,
                color = Color.Black
            )
        }
    }
}

@Composable
fun KeyboardRow(
    keys: List<String>,
    onKeyPress: (String) -> Unit,
    isUpperCase: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keys.forEach { key ->
            KeyboardButton(
                text = key,
                onClick = { onKeyPress(key) },
                isUpperCase = isUpperCase,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun CustomAlphaKeyboard(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onShiftToggle: (() -> Unit)? = null,
    isUpperCase: Boolean = false
) {
    val randomNumbers = remember { (0..9).map { it.toString() }.shuffled() }
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ñ")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    val transformKey: (String?, ImageVector?) -> Unit = { text, icon ->
        when (icon) {
            Icons.Filled.KeyboardCapslock -> onShiftToggle?.invoke()
            Icons.AutoMirrored.Filled.Backspace -> onDelete()
            Icons.AutoMirrored.Filled.KeyboardReturn -> onEnter()
            Icons.Filled.SpaceBar -> onKeyPress(" ")
            null -> text?.let { onKeyPress(if (isUpperCase) it.uppercase() else it) }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            randomNumbers.forEach {
                KeyboardButton(
                    text = it,
                    onClick = { onKeyPress(it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
            }
        }

        listOf(row1, row2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    KeyboardButton(
                        text = key,
                        onClick = { onKeyPress(key) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row3.forEach { key ->
                KeyboardButton(
                    text = key,
                    onClick = { onKeyPress(key) },
                    modifier = Modifier.weight(0.5f)
                )
            }

            KeyboardButton(
                icon = Icons.AutoMirrored.Outlined.Backspace,
                onClick = onDelete,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CustomNumericKeyboard(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit
) {
    val numbers = remember { (0..9).map { it.toString() }.shuffled() }
    val rows = numbers.take(9).chunked(3)
    val lastNumber = numbers.last()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Primeras 3 filas con 3 números cada una
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach {
                    KeyboardButton(
                        text = it,
                        onClick = { onKeyPress(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.weight(1f))

            KeyboardButton(
                text = lastNumber,
                onClick = { onKeyPress(lastNumber) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            )

            KeyboardButton(
                icon = Icons.AutoMirrored.Outlined.Backspace,
                onClick = onDelete,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            )
        }
    }
}

@Composable
fun SafeKeyboard(
    type: KeyboardType,
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onShiftToggle: (() -> Unit)? = null,
    isUpperCase: Boolean = false
) {
    if (type == KeyboardType.Number) {
        CustomNumericKeyboard(
            onKeyPress = onKeyPress,
            onDelete = onDelete,
        )
    } else {
        CustomAlphaKeyboard(
            onKeyPress = onKeyPress,
            onDelete = onDelete,
            onEnter = onEnter,
            onShiftToggle = onShiftToggle,
            isUpperCase = isUpperCase
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomAlphaKeyboardPreview() {
    CustomKeyboardTheme {
        CustomAlphaKeyboard(
            onKeyPress = {},
            onDelete = {},
            onEnter = {},
            onShiftToggle = {},
            isUpperCase = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomNumericKeyboardPreview() {
    CustomKeyboardTheme {
        CustomNumericKeyboard(
            onKeyPress = {},
            onDelete = {},
        )
    }
}