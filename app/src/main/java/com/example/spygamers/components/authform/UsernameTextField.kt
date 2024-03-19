package com.example.spygamers.components.authform

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun UsernameTextField(username: MutableState<String>, message: MutableState<String>) {
    TextField(
        value = username.value,
        onValueChange = {
            username.value = it
            Log.d("Username TextField", it)
            username.value = it
            if (!isValidUsername(username.value)) {
                message.value = "Your Username should have more than 4 characters"
            } else {
                message.value = ""
            }
        },
        label = { Text("Username") },
        placeholder = { Text(text = "Enter username") },
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 8.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
    )
}

internal fun isValidUsername(username: String): Boolean {
    return username.isNotBlank()
}