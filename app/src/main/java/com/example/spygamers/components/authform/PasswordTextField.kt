package com.example.spygamers.components.authform

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun PasswordTextField(
    password: MutableState<String>,
    message: MutableState<String>
) {
    TextField(
        value = password.value,
        onValueChange = {
            password.value = it

            if (!isValidPassword(password.value)) {
                message.value =
                    "Ensure you have more than 4 characters, and less than 16 characters!"
            } else {
                message.value = ""
            }
        },
        isError = !isValidPassword(password.value),
        label = { Text("Password") },
        placeholder = { Text(text = "Enter password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 8.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
    )
}

internal fun isValidPassword(password: String): Boolean {
    return password.length in 4..16
}