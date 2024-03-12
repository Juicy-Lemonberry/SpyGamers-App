package com.example.spygamers

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.spygamers.RetrofitClient.authService
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt


@Composable
fun RegisterScreen(
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
    var isConfirmPasswordValid by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = message,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // Username Text Field
            TextField(
                value = username,
                onValueChange = {
                    username = it

                    if (username.length < 4) {
                        message = "Your Username should have more than 4 characters"
                        isUsernameValid = false
                    } else {
                        message = ""
                        isUsernameValid = true
                    }
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

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                        message = "Invalid Email Format"
                        isEmailValid = false
                    } else {
                        message = ""
                        isEmailValid = true
                    }
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

                    if (!checkPasswordValidity(it)) {
                        message =
                            "Ensure you have more than 8 characters with at least one lower-case, upper-case character, digits & special character"
                        isPasswordValid = false
                    } else {
                        message = ""
                        isPasswordValid = true
                    }
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
                onValueChange = {
                    confirmPassword = it
                    if (password != confirmPassword) {
                        message = "Passwords don't match"
                        isConfirmPasswordValid = false
                    } else {
                        isConfirmPasswordValid = true
                        message = ""
                    }

                },
                isError = !isConfirmPasswordValid,
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
                    if (isUsernameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
                        viewModel.viewModelScope.launch {
                            val hashedPassword = hashPassword(password)

                            val userHashMap = HashMap<String, String>()
                            userHashMap["username"] = username
                            userHashMap["email"] = email
                            userHashMap["password"] = hashedPassword

                            // Username and email are available, proceed with registration using Retrofit
                            val registrationResponse = authService.registerUser(userHashMap)

                            val responseStatus = registrationResponse.body()?.status ?: "Unknown"
                            val sessionToken = registrationResponse.body()?.session_token


                            Log.d("Registration", "User registration successful?")
                            Log.d("Registration", "Response status: $responseStatus")
                            Log.d("Registration", "Session token: $sessionToken")

                            if (registrationResponse.isSuccessful) {
                                // Handle successful registration (navigate to login screen, etc.)
                            } else {
                                // Handle registration failure (display error message)
                            }

                        }
                    }
                },
                enabled = isUsernameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid,
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
}

// Function to check password validity
fun checkPasswordValidity(password: String): Boolean {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSymbol = password.any { !it.isLetterOrDigit() }
    return password.length >= 8 && hasUpperCase && hasLowerCase && hasDigit && hasSymbol
}

// Function to hash password
fun hashPassword(password: String): String {
    // Generate a salt for bcrypt (the log rounds parameter is optional and can be adjusted)
    val salt = BCrypt.gensalt()

    // Hash the password with the generated salt
    return BCrypt.hashpw(password, salt)
}