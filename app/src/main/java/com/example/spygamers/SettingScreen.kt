package com.example.spygamers

import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val context = LocalContext.current

    var timezone by remember { mutableStateOf("") }
    var gameName by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf<ImageBitmap?>(null) }
    var username by remember { mutableStateOf("") }

    var timezoneCode = viewModel.getTimezoneCode()
    var accountID = viewModel.getAccountID()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            profilePicture = bitmap.asImageBitmap()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            if (timezoneCode != null) {
                Text(
                    text = timezoneCode,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            if (accountID != null) {
                Text(
                    text = accountID.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            // Editable field for changing timezone
            TextField(
                value = timezone,
                onValueChange = { timezone = it },
                label = { Text("Timezone") },
                modifier = Modifier.width(225.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Editable field for changing game preferences
            TextField(
                value = gameName,
                onValueChange = { gameName = it },
                label = { Text("Game Name") },
                modifier = Modifier.width(225.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Editable field for changing username
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.width(225.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to upload profile picture from gallery
            Button(onClick = {
                launcher.launch("image/*")
            }) {
                Text("Upload Profile Picture")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (profilePicture != null) {
                Text(
                    text = "New Profile Picture:",
                )

                Image(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    bitmap = profilePicture!!,
                    contentDescription = "Profile Picture"
                )
            }

            // Button to save settings
            Button(onClick = {


                navController.navigate(Screen.SettingScreen.route)
            }) {
                Text("Save")
            }
        }
    }
}