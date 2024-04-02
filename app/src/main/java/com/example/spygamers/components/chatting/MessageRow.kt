package com.example.spygamers.components.chatting

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.directmessaging.DirectMessagingService
import com.example.spygamers.services.directmessaging.GetAttachmentBody
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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
    val isDeletedMessage: Boolean = false,
    val isDirectMessage: Boolean = false
)

@Composable
fun MessageRow(
    message: MessageData,
    sessionToken: String,
    serviceFactory: ServiceFactory,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(message.attachmentsID) {
        if (message.attachmentsID.isNotEmpty()) {
            coroutineScope.launch {
                val attachmentID = message.attachmentsID.first()
                val service = serviceFactory.createService(DirectMessagingService::class.java)
                val response = service.getAttachment(GetAttachmentBody(
                    authToken = sessionToken,
                    attachmentID = attachmentID,
                    attachmentType = if (message.isDirectMessage) "DIRECT_MESSAGE" else "GROUP"
                ))

                if (response.isSuccessful) {
                    // Create a temporary file
                    val tempFile = File.createTempFile("download_", ".tmp", context.cacheDir)
                    response.body()?.byteStream()?.use { inputStream ->
                        FileOutputStream(tempFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // Update the Compose state with the URI of the saved file
                    imageUri = Uri.fromFile(tempFile)
                    return@launch
                }

                Log.w("MessageRow.attachment", "Bad Response attempting to fetch $attachmentID from backend endpoint: ${response.errorBody()}")
            }
        }
    }

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
                    style = MaterialTheme.typography.h6
                )

                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Attachment",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Content of text...
                Text(
                    // Display message only if its not deleted...
                    text = if (!message.isDeletedMessage) message.content else "This message was deleted...",
                    style = MaterialTheme.typography.subtitle2
                )
                // Timestamp of text...
                Text(
                    text = dateFormat.format(message.timestamp),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.End),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}