package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskVisualTransformation(
    private val isPasswordVisible: Boolean,
    private val isCursorVisible: Boolean,
    private val cursorIndex: Int,
    private val originalText: String
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = buildString {
            originalText.forEachIndexed { index, char ->
                if (index == cursorIndex && isCursorVisible) append('|')
                append(if (isPasswordVisible) char else '•')
            }
            if (cursorIndex == originalText.length && isCursorVisible) append('|')
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + if (cursorIndex in 0..<offset && isCursorVisible) 1 else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    cursorIndex < 0 -> offset.coerceAtMost(originalText.length)
                    offset <= cursorIndex -> offset
                    offset == cursorIndex + 1 && isCursorVisible -> cursorIndex
                    else -> originalText.length
                }
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}