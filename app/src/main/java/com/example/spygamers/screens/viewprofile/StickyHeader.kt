package com.example.spygamers.screens.viewprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StickyHeader(username: String, isViewingSelf: Boolean, onEditClick: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            // Placeholder for user's icon/avatar
            Icon(Icons.Default.Person, contentDescription = "User Icon", modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = username, style = MaterialTheme.typography.h6)

            if (isViewingSelf) {
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onEditClick("Username") }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }

        if (isViewingSelf) {
            Text(
                text = "Tap to edit",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }
    }
}
