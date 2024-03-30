package com.example.spygamers.components.chatting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MessageData(
    val messageID: Int,
    val authorUsername: String,
    val content: String,
    val timestamp: Date,
    val attachmentsID: List<Int>,
    val senderIsSelf: Boolean = false,
    val isDeletedMessage: Boolean = false
)

@Composable
fun MessageRow(message: MessageData) {
    // TODO: Display attachments...
    // TODO: Implement callback for delete requests or edit requests...
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        // Align towards right side if sender is self...
        horizontalArrangement = if (message.senderIsSelf) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            // Color it primary if sender is self.
            color = if (message.senderIsSelf) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        ) {
            Column(Modifier.padding(8.dp)) {
                // Username...
                Text(
                    text = message.authorUsername,
                    style = MaterialTheme.typography.body1
                )
                // Content of text...
                Text(
                    // Display message only if its not deleted...
                    text = if (!message.isDeletedMessage) message.content else "This message was deleted...",
                    style = MaterialTheme.typography.subtitle1
                )
                // Timestamp of text...
                Text(
                    text = dateFormat.format(message.timestamp),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}