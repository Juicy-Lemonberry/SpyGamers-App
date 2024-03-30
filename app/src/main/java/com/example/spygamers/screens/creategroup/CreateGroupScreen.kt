package com.example.spygamers.screens.creategroup

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.Friendship
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.friendship.FriendshipService
import com.example.spygamers.services.group.GroupService
import com.example.spygamers.services.group.body.CreateGroupBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateGroupScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                navigationIconImage = Icons.Default.ArrowBack,
                navigationIconDescription = "Back Button",
                onNavigationIconClick = {
                    navController.popBackStack()
                },
                appBarTitle = "Create a New Group"
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
    ) {
        MainBody(navController, viewModel)
    }
}

@Composable
private fun MainBody(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val context = LocalContext.current
    // Example setup, replace with your actual implementation
    val serviceFactory = ServiceFactory()
    val sessionToken by viewModel.sessionToken.collectAsState()

    var friends by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var groupName by rememberSaveable { mutableStateOf("") }
    var groupDescription by rememberSaveable { mutableStateOf("") }
    var isPublic by rememberSaveable { mutableStateOf(false) }
    var selectedFriends by rememberSaveable { mutableStateOf(setOf<Int>()) }

    // Fetch list of friends related to friend...
    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            val service = serviceFactory.createService(FriendshipService::class.java)
            val response = service.getFriends(AuthOnlyBody(sessionToken))
            if (response.isSuccessful) {
                val fullList: List<Friendship> = response.body()?.friends ?: emptyList()
                friends = fullList.filter { it.status == "ACCEPTED" }
            } else {
                // Handle error
                Log.e("FriendService", "Failed to get friends: ${response.message()}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // TODO: Allow setting of initial group icon
                Icon(Icons.Default.AccountCircle, contentDescription = "Group Icon")
                Spacer(modifier = Modifier.width(4.dp))

                // Name of Group
                TextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Name of Group") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Group description
            TextField(
                value = groupDescription,
                onValueChange = { groupDescription = it },
                label = { Text("Description of Group") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Public group toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Make this group public?")
                Checkbox(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it }
                )
            }

            Divider()

            // Header
            Text("Select friends to add to group", style = MaterialTheme.typography.h6)

            // List of friends to add into the group...
            LazyColumn {
                items(friends.size) { index ->
                    val friend = friends[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(friend.username)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = selectedFriends.contains(friend.accountID),
                            onCheckedChange = { isChecked ->
                                selectedFriends = if (isChecked) {
                                    selectedFriends + friend.accountID
                                } else {
                                    selectedFriends - friend.accountID
                                }
                            }
                        )
                    }
                }
            }
        }

        // FAB To finalize group creation
        FloatingActionButton(
            onClick = {
                if (groupName.isEmpty()) {
                    Toast.makeText(context, "No group name set!", Toast.LENGTH_SHORT).show()
                    return@FloatingActionButton
                }

                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val service = serviceFactory.createService(GroupService::class.java)
                        val response = service.createGroup(
                            CreateGroupBody(
                                authToken = sessionToken,
                                groupName = groupName,
                                groupDescription = groupDescription,
                                isPublic = isPublic
                            )
                        )

                        if (!response.isSuccessful) {
                            Log.e("CreateGroupScreen.createGroup", "Failed to create group :: $response")
                            return@launch
                        }

                        // Fetch body response, ensure its not blank...
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e("CreateGroupScreen.createGroup", "Response body is null")
                            return@launch
                        }

                        // If group created successfully, populate in ViewModel,
                        // then redirect to the group chat itself...
                        val groupID = responseBody.groupID
                        viewModel.setTargetGroup(groupID, groupName, groupDescription, isPublic)

                        navController.popBackStack()
                        navController.navigate(Screen.GroupMessageScreen.route)
                    } catch (e: Exception) {
                        Log.e("DirectMessageScreen.sendMessageToUser", "Error sending message :: ", e)
                    }
                }
            },
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd)
        ) {
            Text("Create")
        }
    }
}