package com.example.spygamers.screens.register

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
internal fun EmailTextField(email: MutableState<String>, message: MutableState<String>) {
    TextField(
        value = email.value,
        onValueChange = {
            email.value = it

            if (!isValidEmail(email.value)) {
                message.value = "Invalid Email Format"
            } else {
                message.value = ""
            }
        },
        isError = !isValidEmail(email.value),
        label = { Text("Email") },
        placeholder = { Text(text = "Enter email") },
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 8.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
    )
}

internal fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}