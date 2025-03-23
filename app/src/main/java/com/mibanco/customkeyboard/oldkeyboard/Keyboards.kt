package com.mibanco.customkeyboard.oldkeyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RandomNumericKeyboard(onKeyPress: (String) -> Unit) {
    val numbers = remember { (0..9).shuffled() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.chunked(3).forEach { rowNumbers ->
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                rowNumbers.forEach { number ->
                    Button(
                        onClick = { onKeyPress(number.toString()) },
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape
                    ) {
                        Text(text = number.toString(), fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AlphanumericKeyboard(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onShiftToggle: () -> Unit,
    isUpperCase: Boolean
) {
    val numbers = remember { (0..9).shuffled() }
    val letters = remember { ('A'..'Z').toList() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.chunked(3).forEach { rowNumbers ->
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                rowNumbers.forEach { number ->
                    Button(
                        onClick = { onKeyPress(number.toString()) },
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape
                    ) {
                        Text(text = number.toString(), fontSize = 24.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        letters.chunked(7).forEach { rowLetters ->
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                rowLetters.forEach { letter ->
                    Button(
                        onClick = { onKeyPress(letter.toString()) },
                        modifier = Modifier.size(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = if (isUpperCase) letter.toString() else letter.toString().lowercase(), fontSize = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botones especiales
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = onShiftToggle,
                modifier = Modifier.size(80.dp)
            ) {
                Text("Mayús")
            }

            Button(
                onClick = onDelete,
                modifier = Modifier.size(80.dp)
            ) {
                Text("← Borrar")
            }

            Button(
                onClick = onEnter,
                modifier = Modifier.size(80.dp)
            ) {
                Text("✔ Enter")
            }
        }
    }
}

