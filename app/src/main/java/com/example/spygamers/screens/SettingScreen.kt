package com.example.spygamers.screens

import android.annotation.SuppressLint
import android.provider.MediaStore
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.Screen
import com.example.spygamers.components.AppBar
import com.example.spygamers.components.DrawerBody
import com.example.spygamers.components.DrawerHeader
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.profilechanger.ChangeUsernameBody
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(Screen.SettingScreen),
                onItemClick = {item ->
                    handleDrawerItemClicked(item, Screen.SettingScreen, navController)
                }
            )
        }
    ) {
        MainBody(viewModel, navController);
    }
}

@Composable
private fun MainBody(viewModel: GamerViewModel, navController: NavController){
    var gameName by rememberSaveable { mutableStateOf("") }
    var profilePicture by rememberSaveable { mutableStateOf<ImageBitmap?>(null) }
    val username by viewModel.username.collectAsState()
    val sessionToken by viewModel.sessionToken.collectAsState()

    // TODO: Load profile picture from backend server instead...
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
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
                style = MaterialTheme.typography.h2,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            /*
            // TODO: Load dynamically from server instead...
            // Editable field for changing timezone
            timezone?.let {
                TextField(
                    value = it,
                    onValueChange = { timezone = it },
                    label = { Text("Timezone") },
                    modifier = Modifier.width(225.dp)
                )
            }
            *
             */

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
            username?.let {
                TextField(
                    value = it,
                    onValueChange = { /* TODO: Change username... */ },
                    label = { Text("Username") },
                    modifier = Modifier.width(225.dp)
                )
            }

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
                viewModel.viewModelScope.launch {
                    val newUserName = sessionToken?.let { username?.let { it1 -> ChangeUsernameBody(it, it1) } }


                    val service = ServiceFactory().createProfileChangerService();

                    val retrofitResponse = newUserName?.let { service.changeUsername(it) }

                    if (retrofitResponse != null) {
                        if (retrofitResponse.isSuccessful) {

                            navController.navigate(Screen.SettingScreen.route)
                        }
                    }
                }
            }) {
                Text("Save")
            }
        }
    }
}