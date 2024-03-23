package com.example.spygamers.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * @param field Name of the field (Ex. Timezone)
 * @param currentValue Original value, represented in IBM Code.
 * @param onDismiss When user dismiss this dialog without saving.
 * @param onSave When user saves the new value. (String args in IBM Code)
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditTimezoneDialog(
    field: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val timezoneMap = listOf(
        "B" to "GMT +1", "C" to "GMT +2", "D" to "GMT +3", "E" to "GMT +4", "F" to "GMT +5",
        "G" to "GMT +6", "H" to "GMT +7", "I" to "GMT +8", "K" to "GMT +9", "L" to "GMT +10",
        "M" to "GMT +11", "N" to "GMT +12", "O" to "GMT -1", "P" to "GMT -2", "Q" to "GMT -3",
        "R" to "GMT -4", "S" to "GMT -5", "T" to "GMT -6", "U" to "GMT -7", "V" to "GMT -8",
        "W" to "GMT -9", "X" to "GMT -10", "Y" to "GMT -11", "Z" to "GMT -12"
    ).associate { it }

    // Find the current value's corresponding timezone string to display as the initial value in the dropdown.
    val initialTimezoneString = timezoneMap[currentValue] ?: timezoneMap.values.first()
    var selectedTimezoneString by rememberSaveable { mutableStateOf(initialTimezoneString) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit $field") },
        text = {
            Column {
                Text(text = "Select new $field:")
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedTimezoneString,
                        onValueChange = { },
                        label = { Text("Timezone") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        singleLine = true,
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        timezoneMap.forEach { (code, timezone) ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedTimezoneString = timezone
                                    expanded = false
                                }
                            ) {
                                Text(text = timezone)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Find the code corresponding to the selected timezone string and return it in onSave
                val selectedCode = timezoneMap.entries.firstOrNull { it.value == selectedTimezoneString }?.key ?: ""
                onSave(selectedCode)
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