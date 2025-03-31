package com.mibanco.customkeyboard.safeKeyboard

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class SafeTextFieldListController(
    private val fields: SnapshotStateList<TextFieldValue>,
    private val focusedFieldIndex: MutableIntState
) {
    fun insertText(key: String) {
        val index = focusedFieldIndex.intValue
        if (index in fields.indices) {
            val old = fields[index]
            val cursor = old.selection.start
            val newText = old.text.substring(0, cursor) + key + old.text.substring(cursor)
            fields[index] = TextFieldValue(
                text = newText,
                selection = TextRange(cursor + key.length)
            )
        }
    }

    fun deleteText() {
        val index = focusedFieldIndex.intValue
        if (index in fields.indices) {
            val old = fields[index]
            val cursor = old.selection.start
            if (cursor > 0) {
                val newText = old.text.removeRange(cursor - 1, cursor)
                fields[index] = TextFieldValue(
                    text = newText,
                    selection = TextRange(cursor - 1)
                )
            }
        }
    }

    fun setText(index: Int, value: TextFieldValue) {
        if (index in fields.indices) {
            fields[index] = value
        }
    }

    fun getText(index: Int): TextFieldValue {
        return fields.getOrNull(index) ?: TextFieldValue()
    }
}