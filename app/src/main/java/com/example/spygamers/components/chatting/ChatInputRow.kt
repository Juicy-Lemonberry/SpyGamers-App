package com.example.spygamers.components.chatting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputRow(
    onUserSendMessage: (message: String) -> Unit
){
    val userMessage = rememberSaveable { mutableStateOf("") }

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
            placeholder = { Text("Type a message...") }
        )

        Button(
            onClick = {
                if (userMessage.value.isNotEmpty()) {
                    onUserSendMessage(userMessage.value)
                    userMessage.value = ""
                }
            }
        ) {
            Text("SEND")
        }
    }
}