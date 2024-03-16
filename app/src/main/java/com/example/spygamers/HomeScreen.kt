package com.example.spygamers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spygamers.ui.theme.Purple40

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(
                text = "Home Screen",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    // Navigate to settings screen
                },
                modifier = Modifier
                    .padding(end = 6.dp)
                    .width(180.dp)
                    .height(52.dp) // Adjust the height of the button
            ) {
                Text("Settings")
            }

            Button(
                onClick = {
                    // Navigate to friend recommendations screen
                },
                modifier = Modifier
                    .padding(start = 6.dp)
                    .width(180.dp)
                    .height(52.dp) // Adjust the height of the button
            ) {
                Text("Friend Recommendations")
            }
        }
    }
}

