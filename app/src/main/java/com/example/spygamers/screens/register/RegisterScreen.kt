package com.example.spygamers.screens.register

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.authform.PasswordTextField
import com.example.spygamers.components.authform.UsernameTextField
import com.example.spygamers.components.authform.isValidPassword
import com.example.spygamers.components.authform.isValidUsername
import com.example.spygamers.components.recommendChecker.ContactsChecker
import com.example.spygamers.components.recommendChecker.LocationChecker
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.authentication.UserRegistrationBody
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
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

    var inputEmail = rememberSaveable {
        mutableStateOf("")
    }

    var inputConfirmPassword = rememberSaveable {
        mutableStateOf("")
    }

    var message = rememberSaveable { mutableStateOf("") }

    val isEmulator = ((Build.MANUFACTURER == "Google" && Build.BRAND == "google" &&
            ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                    && Build.FINGERPRINT.endsWith(":user/release-keys")
                    && Build.PRODUCT.startsWith("sdk_gphone_")
                    && Build.MODEL.startsWith("sdk_gphone_"))
                    //alternative
                    || (Build.FINGERPRINT.startsWith("google/sdk_gphone64_")
                    && (Build.FINGERPRINT.endsWith(":userdebug/dev-keys") || Build.FINGERPRINT.endsWith(":user/release-keys"))
                    && Build.PRODUCT.startsWith("sdk_gphone64_")
                    && Build.MODEL.startsWith("sdk_gphone64_"))))
            //
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            //bluestacks
            || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
            //bluestacks
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HOST.startsWith("Build")
            //MSI App Player
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.PRODUCT == "google_sdk"
            )

    if (!isEmulator) {
        LocationChecker(viewModel, context)
        ContactsChecker(viewModel = viewModel, context = context)
    }

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
                style = MaterialTheme.typography.h2,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = message.value,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            UsernameTextField(inputUsername, message);
            EmailTextField(inputEmail, message);
            PasswordTextField(inputPassword, message);
            ConfirmPasswordTextField(inputConfirmPassword, message) { newPass ->
                newPass == inputPassword.value
            }

            // Sign Up Button with validation check
            Button(
                onClick = {
                    val user = UserRegistrationBody(
                        inputUsername.value,
                        inputPassword.value,
                        inputEmail.value
                    )
                    viewModel.viewModelScope.launch {
                        val authService = ServiceFactory().createAuthenticationService()
                        val response = authService.registerUser(user)
                        Log.d("RegisterScreen", "Send register...")

                        if (!response.isSuccessful) {
                            Log.d("RegisterScreen", "ERR :: " + response.errorBody().toString())
                            Log.d("RegisterScreen", "MSG :: " +  response.message())
                            Log.d("RegisterScreen", "RAW :: " + response.raw().toString())
                            // TODO: Toast message

                            return@launch
                        }

                        val bodyResponse = response.body()!!;

                        if (bodyResponse.status == "SUCCESS") {
                            // TODO: Toast, Short Delay, then navigate...
                            navController.navigate(Screen.LoginScreen.route)
                            return@launch
                        }

                        var toastMessage = "Unknown Failure, try again later!";
                        if (bodyResponse.status == "EMAIL_TAKEN") {
                            toastMessage = "The email has been taken!"
                        } else if (bodyResponse.status == "USERNAME_TAKEN") {
                            toastMessage = "The username has been taken!"
                        }
                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isValidInputs(
                    inputUsername.value,
                    inputEmail.value,
                    inputPassword.value,
                    inputConfirmPassword.value
                ),
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

private fun isValidInputs(username: String, email: String, password: String, confirmPassword: String): Boolean {
    return isValidUsername(username) && isValidEmail(email) && isValidPassword(password) && (password == confirmPassword)
}
