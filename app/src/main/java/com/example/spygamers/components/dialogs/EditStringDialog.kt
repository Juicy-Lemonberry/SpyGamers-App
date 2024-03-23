package com.example.spygamers.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 *
 * @param field Name of the field being edited. (Ex. 'Username')
 * @param currentValue Current value to be edited.
 * @param onDismiss Callback when the dialog is dismissed without saving.
 * @param onSave Callback when the new value is saved.
 */
@Composable
fun EditStringDialog(
    field: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val (text, setText) = rememberSaveable { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit $field") },
        text = {
            Column {
                Text(text = "Enter new $field:")
                TextField(
                    value = text,
                    onValueChange = setText,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(text)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
