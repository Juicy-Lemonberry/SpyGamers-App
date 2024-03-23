package com.example.spygamers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun NonEditableField(
    label: String,
    value: String,
    labelTextStyle: TextStyle = MaterialTheme.typography.subtitle2
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(text = label, style = labelTextStyle)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.body1)
    }
}
