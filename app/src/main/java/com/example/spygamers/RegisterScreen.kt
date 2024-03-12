package com.example.spygamers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
fun registerScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isUsernameValid by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp), // Add horizontal padding for better margins
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Username Text Field
        TextField(
            value = username,
            onValueChange = {
                username = it
                isUsernameValid = username.length >= 4 //Minimum of 4 characters
            },
            isError = !isUsernameValid,
            label = { Text("Username") },
            placeholder = { Text(text = "Enter username") },
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 8.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        )

        // Email Text Field
        TextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
            },
            isError = !isEmailValid,
            label = { Text("Email") },
            placeholder = { Text(text = "Enter email") },
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 8.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        )

        // Password Text Field
        TextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid = checkPasswordValidity(it)
            },
            isError = !isPasswordValid,
            label = { Text("Password") },
            placeholder = { Text(text = "Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 8.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        )

        // Confirm Password Text Field
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            isError = password != confirmPassword,
            label = { Text("Confirm Password") },
            placeholder = { Text(text = "Re-enter password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 8.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
        )

        // Sign Up Button with validation check
        Button(
            onClick = {
                if (isUsernameValid && isEmailValid && isPasswordValid) {
                    viewModel.viewModelScope.launch {
                        val hashedPassword = hashPassword(password)
                        val user =
                            Gamer(username = username, email = email, password = hashedPassword)

                        val isUsernameAvailable = viewModel.checkUsernameAvailability(username)
                        val isEmailAvailable = viewModel.checkEmailAvailability(email)

                        if (isUsernameAvailable && isEmailAvailable) {
                            viewModel.insertUser(user)
                            navController.navigate(Screen.LoginScreen.route)
                        } else {
                            // Display appropriate error messages based on availability
                            if (!isUsernameAvailable) {
                                // Username already exists
                            }
                            if (!isEmailAvailable) {
                                // Email already exists
                            }
                        }
                    }
                }
            },
            enabled = isUsernameValid && isEmailValid && isPasswordValid,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Sign Up")
        }

        //Back Button to Login Screen
        Button(
            onClick = {
                navController.navigate(Screen.LoginScreen.route)
            }
        ) {
            Text("Back")
        }
    }
}

// Function to check password validity (example)
fun checkPasswordValidity(password: String): Boolean {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSymbol = password.any { !it.isLetterOrDigit() }
    return password.length >= 8 && hasUpperCase && hasLowerCase && hasDigit && hasSymbol
}

// Function to hash password (replace with a secure hashing algorithm)
fun hashPassword(password: String): String {
    // Implement a secure password hashing algorithm like bcrypt
    return "hashed_password"
}

/*suspend fun isValidEmail(email: String,  viewModel: GamerViewModel): ValidationResult {
    val emailRegex = Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
    if (!email.matches(emailRegex)) {
        return ValidationResult.Invalid("Invalid email format")
    }

    val isEmailUnique = viewModel.isEmailUnique(email)
    return if (!isEmailUnique) {
        ValidationResult.Invalid("Email already exists")
    } else {
        ValidationResult.Valid
    }
}
*/