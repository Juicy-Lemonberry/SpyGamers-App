package com.example.spygamers.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.Screen
import com.example.spygamers.components.authform.PasswordTextField
import com.example.spygamers.components.authform.UsernameTextField
import com.example.spygamers.components.authform.isValidPassword
import com.example.spygamers.components.authform.isValidUsername
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.authentication.UserLoginBody
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val context = LocalContext.current
    var inputUsername = rememberSaveable {
        mutableStateOf("")
    }
    var inputPassword = rememberSaveable {
        mutableStateOf("")
    }

    var message = rememberSaveable { mutableStateOf("") }

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
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = message.value,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        UsernameTextField(inputUsername, message)
        PasswordTextField(inputPassword, message)

        // Login Button
        Button(
            onClick = {

                var filledUsername = inputUsername.value
                viewModel.viewModelScope.launch {
                    val userCredentials = UserLoginBody(filledUsername, inputPassword.value)
                    val authService = ServiceFactory().createAuthenticationService()
                    val response = authService.userLogin(userCredentials)

                    if (!response.isSuccessful){
                        Log.d("LoginScreen", "ERR :: " + response.errorBody().toString())
                        Log.d("LoginScreen", "MSG :: " +  response.message())
                        Log.d("LoginScreen", "RAW :: " + response.raw().toString())
                        // TODO: Toast Message...
                        return@launch
                    }

                    val responseBody = response.body()!!;
                    if (responseBody.status == "SUCCESS") {
                        // TODO: Toast, Short Delay, Login...
                        val sessionToken = responseBody.sessionToken!!
                        viewModel.upsertUserData(
                            sessionToken,
                            filledUsername,
                            responseBody.accountID!!
                        )
                        navController.navigate(Screen.HomeScreen.route)
                        return@launch
                    }

                    var toastMessage = "Unknown Failure, try again later!";
                    if (responseBody.status == "USERNAME_INVALID") {
                        toastMessage = "The username doesn't exists! Register?"
                    } else if (responseBody.status == "PASSWORD_INVALID") {
                        toastMessage = "The password is incorrect!"
                    }
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                }
            },
            enabled = isValidInputs(inputUsername.value, inputPassword.value),
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

private fun isValidInputs(username: String, password: String) : Boolean {
    return isValidPassword(password) && isValidUsername(username)
}