package com.example.spygamers

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun FriendListScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {

    var auth_token by remember { mutableStateOf("") }

    auth_token = viewModel.getSessionToken().toString()

    var friends by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var incomingRequests by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var outgoingRequests by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var acceptedFriends by remember { mutableStateOf<List<Friend>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {

            val retrofit = Retrofit.Builder()
                .baseUrl("http://spygamers.servehttp.com:44414/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(FriendService::class.java)

            val response = service.getFriends(getFriend(auth_token))
            if (response.isSuccessful) {
                friends = response.body()?.friends ?: emptyList()
                incomingRequests = friends.filter { it.status == "INCOMING_REQUEST" }
                outgoingRequests = friends.filter { it.status == "OUTGOING_REQUEST" }
                acceptedFriends = friends.filter { it.status == "ACCEPTED" }
            } else {
                // Handle error
                Log.e("FriendService", "Failed to get friends: ${response.message()}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FriendListAppBar(navController)

        fun removeIncomingRequest(idToRemove: Int) {
            incomingRequests = incomingRequests.filter { it.account_id != idToRemove }
        }

        fun removeOutgoingRequest(idToRemove: Int) {
            outgoingRequests = outgoingRequests.filter { it.account_id != idToRemove }
        }

        fun removeAcceptedFriend(idToRemove: Int) {
            acceptedFriends = acceptedFriends.filter { it.account_id != idToRemove }
        }

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
            // Code for incoming requests
            items(if (incomingRequests.isEmpty()) 1 else incomingRequests.size) { index ->
                if (incomingRequests.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile picture (dummy)
                        // Dummy profile picture
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "No Incoming Requests",
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxSize()
                        )
                    }
                } else {
                    val friend = incomingRequests[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile picture (dummy)
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Magenta)
                                .padding(8.dp)
                        )

                        // Friend name
                        Text(
                            text = "Incoming Request from ${friend.username}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp)
                        )

                        // Accept icon
                        IconButton(
                            onClick = { /* Handle accept action */ },
                            modifier = Modifier
                                .padding(4.dp)
                                .background(Color.Green)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Accept",
                                tint = Color.White
                            )
                        }

                        // Reject icon
                        IconButton(
                            onClick =
                            {
                                viewModel.viewModelScope.launch {

                                    val retrofit = Retrofit.Builder()
                                        .baseUrl("http://spygamers.servehttp.com:44414/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()

                                    val service = retrofit.create(FriendService::class.java)

                                    val response = service.removeFriends(
                                        removeFriend(
                                            friend.account_id,
                                            auth_token
                                        )
                                    )
                                    if (response.isSuccessful) {
                                        removeIncomingRequest(friend.account_id)
                                    } else {
                                        // Handle error
                                        Log.e(
                                            "FriendService",
                                            "Failed to get friends: ${response.message()}"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .background(Color.Red)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Reject",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Repeat similar logic for outgoing requests and friends

            // Outgoing Requests
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
            // Outgoing Requests
            items(if (outgoingRequests.isEmpty()) 1 else outgoingRequests.size) { index ->
                if (outgoingRequests.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "No Outgoing Requests",
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxSize()
                        )
                    }
                } else {
                    val friend = outgoingRequests[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile picture (dummy)
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Magenta)
                                .padding(8.dp)
                        )

                        // Friend name
                        Text(
                            text = "Outgoing Request to ${friend.username}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp)
                        )

                        // Reject icon
                        IconButton(
                            onClick = {
                                viewModel.viewModelScope.launch {

                                    val retrofit = Retrofit.Builder()
                                        .baseUrl("http://spygamers.servehttp.com:44414/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()

                                    val service = retrofit.create(FriendService::class.java)

                                    val response = service.removeFriends(
                                        removeFriend(
                                            friend.account_id,
                                            auth_token
                                        )
                                    )
                                    if (response.isSuccessful) {
                                        removeOutgoingRequest(friend.account_id)
                                    } else {
                                        // Handle error
                                        Log.e(
                                            "FriendService",
                                            "Failed to get friends: ${response.message()}"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .background(Color.Red)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Reject",
                                tint = Color.White
                            )
                        }
                    }
                }
            }


            // Friends
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
            // Friends
            items(if (acceptedFriends.isEmpty()) 1 else acceptedFriends.size) { index ->
                if (acceptedFriends.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile picture (dummy)
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Text(
                            text = "No Friends",
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxSize()
                        )
                    }
                } else {
                    val friend = acceptedFriends[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile picture (dummy)
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Magenta)
                                .padding(8.dp)
                        )
                        // Friend name
                        Text(
                            text = "Friend: ${friend.username}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp)
                        )
                        // Reject icon
                        IconButton(
                            onClick = {
                                viewModel.viewModelScope.launch {

                                    val retrofit = Retrofit.Builder()
                                        .baseUrl("http://spygamers.servehttp.com:44414/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()

                                    val service = retrofit.create(FriendService::class.java)

                                    val response = service.removeFriends(
                                        removeFriend(
                                            friend.account_id,
                                            auth_token
                                        )
                                    )
                                    if (response.isSuccessful) {
                                        removeAcceptedFriend(friend.account_id)
                                    } else {
                                        // Handle error
                                        Log.e(
                                            "FriendService",
                                            "Failed to get friends: ${response.message()}"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .background(Color.Red)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Reject",
                                tint = Color.White
                            )
                        }
                    }
                }
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
        },
        colors = TopAppBarDefaults.topAppBarColors(
            colorResource(id = R.color.purple_500),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
        ),
    )
}