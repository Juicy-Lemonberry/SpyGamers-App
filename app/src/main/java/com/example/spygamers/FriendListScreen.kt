package com.example.spygamers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FriendListScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    var auth_token by remember { mutableStateOf("") }

    auth_token = viewModel.getSessionToken().toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FriendListAppBar(navController)

        LazyColumn {
            item {
                Box(
                    modifier = Modifier
                        .background(Color.Gray)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Incoming Requests",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            items(5) { index ->
                Text(
                    text = "Incoming Request $index",
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxSize()
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .background(Color.Gray)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Outgoing Requests",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            items(5) { index ->
                Text(
                    text = "Outgoing Request $index",
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxSize()
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .background(Color.Gray)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Friends",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            items(5) { index ->
                Text(
                    text = "Friend $index",
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = "Friend List",
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(Screen.HomeScreen.route)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            colorResource(id = R.color.purple_500),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
        ),
    )
}
