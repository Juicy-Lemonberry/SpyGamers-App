package com.example.spygamers.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.R
import com.example.spygamers.Screen
import com.example.spygamers.components.AppBar
import com.example.spygamers.components.DrawerBody
import com.example.spygamers.components.DrawerHeader
import com.example.spygamers.models.Friendship
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.friendship.RemoveFriendBody
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FriendListScreen(
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
                items = generateDefaultDrawerItems(),
                onItemClick = {item ->
                    handleDrawerItemClicked(item, Screen.FriendListScreen, navController)
                }
            )
        }
    ) {
        MainBody(navController, viewModel);
    }
}

@Composable
private fun MainBody(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val serviceFactory = ServiceFactory()
    var auth_token by rememberSaveable { mutableStateOf("") }

    auth_token = viewModel.getSessionToken().toString()

    var friends by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var incomingRequests by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var outgoingRequests by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var acceptedFriends by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            val service = serviceFactory.createFriendshipService()
            val response = service.getFriends(AuthOnlyBody(auth_token))
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
                        style = MaterialTheme.typography.h2,
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

                                    val service = serviceFactory.createFriendshipService();

                                    val response = service.removeFriends(
                                        RemoveFriendBody(
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
                        style = MaterialTheme.typography.h2,
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

                                    val service = serviceFactory.createFriendshipService();

                                    val response = service.removeFriends(
                                        RemoveFriendBody(
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
                        style = MaterialTheme.typography.h2,
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
                                    val service = serviceFactory.createFriendshipService();

                                    val response = service.removeFriends(
                                        RemoveFriendBody(
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