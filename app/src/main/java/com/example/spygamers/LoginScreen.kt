package com.example.spygamers

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Text
        Text(
            text = "Gamers",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = message,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Username Text Field
        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Username") },
            label = { Text("Username") },
            modifier = Modifier
                .width(280.dp)
                .padding(bottom = 16.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp)),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        )

        // Password Text Field
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .width(280.dp)
                .padding(bottom = 16.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp)),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {})
        )

        // Login Button
        Button(
            onClick = {
                viewModel.viewModelScope.launch {
                    val userCredentials = UserLogin(username, password)

                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://spygamers.servehttp.com:44414/app-api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val authService = retrofit.create(AuthenticationService::class.java)

                    val retrofitResponse = authService.userLogin(userCredentials)
                    val errorBody = retrofitResponse.errorBody()?.string()
                    val sessionToken = retrofitResponse.body()?.session_token ?: "Unknown"

                    Log.d("Login", "SessionToken: $sessionToken")

                    if (retrofitResponse.isSuccessful) {
                        viewModel.insertOrUpdateSessionToken(sessionToken)

                        navController.navigate(Screen.HomeScreen.route)// to replace
                    } else {
                        if (errorBody != null) {
                            val parsedError = JSONObject(errorBody)
                            val errorMessage = parsedError.getString("status")

                            message = errorMessage
                        }
                    }
                }
            },
            enabled = username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .width(150.dp)
                .padding(8.dp)
        ) {
            Text("Login")
        }

        // Register Button
        Button(
            onClick = {
                // Navigate to Register screen
                navController.navigate(Screen.RegisterScreen.route)
            },
            modifier = Modifier
                .width(150.dp)
                .padding(8.dp)
        ) {
            Text("Register")
        }
    }
}
