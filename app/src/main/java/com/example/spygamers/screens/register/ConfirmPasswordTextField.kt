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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun ConfirmPasswordTextField(
    password: MutableState<String>,
    message: MutableState<String>,
    validationFun: (confirmPass: String) -> Boolean = { false }
) {
    TextField(
        value = password.value,
        onValueChange = {
            password.value = it
            if (!validationFun(password.value)) {
                message.value = "Passwords don't match"
            } else {
                message.value = ""
            }

        },
        isError = !validationFun(password.value),
        label = { Text("Confirm Password") },
        placeholder = { Text(text = "Re-enter password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 8.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
    )
}