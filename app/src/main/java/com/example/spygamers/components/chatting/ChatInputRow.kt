package com.example.spygamers.components.chatting

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ChatInputRow(
    onUserSendMessage: (message: String, imageUri: Uri?) -> Unit
){
    val userMessage = rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                imageUri = it
            }
        }
    )

    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 2.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = userMessage.value,
            onValueChange = { userMessage.value = it },
            modifier = Modifier
                .weight(1f)
                .padding(end = 2.dp),
            placeholder = { Text("Type a message...") },
            trailingIcon = {
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Image Attachments"
                    )
                }
            }
        )

        IconButton(
            onClick = {
                if (userMessage.value.isNotEmpty()) {
                    onUserSendMessage(userMessage.value, imageUri)
                    userMessage.value = ""
                    imageUri = null
                }
            }
        ) {
            Icon(
                Icons.Filled.Send,
                contentDescription = "Send Message"
            )
        }
    }

    imageUri?.let { uri ->
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Selected Image",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Fit
        )
    }
}