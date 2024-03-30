package com.example.spygamers.screens.friendlist

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.Friendship
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.friendship.AddFriendBody
import com.example.spygamers.services.friendship.FriendshipService
import com.example.spygamers.services.friendship.RemoveFriendBody
import kotlinx.coroutines.launch

/**
 * To store the state of the currently select tab in UI
 */
private enum class FriendTab(val title: String) {
    Friends("Friends"),
    IncomingRequests("Incoming Requests"),
    OutgoingRequests("Outgoing Requests")
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FriendListScreen(
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
                appBarTitle = "Friend Lists"
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
    val serviceFactory = ServiceFactory()
    val sessionToken by viewModel.sessionToken.collectAsState()

    var friends by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var incomingRequests by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var outgoingRequests by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }
    var acceptedFriends by rememberSaveable { mutableStateOf<List<Friendship>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            val service = serviceFactory.createService(FriendshipService::class.java)
            val response = service.getFriends(AuthOnlyBody(sessionToken))
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

    // State for the currently selected tab
    var selectedTab by rememberSaveable { mutableStateOf(FriendTab.Friends) }

    //#region Helper functions to remove items from friendship lists...
    fun removeIncomingRequest(idToRemove: Int) {
        incomingRequests = incomingRequests.filter { it.accountID != idToRemove }
    }
    fun addFriend(idToAdd: Int){
        acceptedFriends += incomingRequests.filter {it.accountID == idToAdd}
    }
    fun removeOutgoingRequest(idToRemove: Int) {
        outgoingRequests = outgoingRequests.filter { it.accountID != idToRemove }
    }

    fun removeAcceptedFriend(idToRemove: Int) {
        acceptedFriends = acceptedFriends.filter { it.accountID != idToRemove }
    }
    //#endregion

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            FriendTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTab.ordinal,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) }
                )
            }
        }

        // Content based on the selected tab
        when (selectedTab) {
            FriendTab.Friends -> FriendsTabContent(
                acceptedFriends,
                onRemoveFriend = {accountID ->
                    viewModel.viewModelScope.launch {
                        val service = serviceFactory.createService(FriendshipService::class.java)

                        val response = service.removeFriends(
                            RemoveFriendBody(
                                accountID,
                                sessionToken
                            )
                        )
                        if (response.isSuccessful) {
                            removeAcceptedFriend(accountID)
                        } else {
                            Toast.makeText(context, "Failed to remove friend...", Toast.LENGTH_SHORT).show()
                            Log.e(
                                "FriendService",
                                "Failed to get friends: ${response.message()}"
                            )
                        }
                    }
                },
                onFriendSelected = {friend ->
                    viewModel.setDirectMessageTarget(friend.accountID, friend.username)
                    navController.navigate(Screen.DirectMessageScreen.route)
                }
            )

            FriendTab.IncomingRequests -> IncomingRequestsTabContent(
                    incomingRequests,
                    onAcceptRequest = {accountID ->
                        viewModel.viewModelScope.launch {

                            val service = serviceFactory.createService(FriendshipService::class.java)

                            val response = service.addFriends(
                                AddFriendBody(
                                    accountID,
                                    sessionToken
                                )
                            )
                            if (response.isSuccessful) {
                                removeIncomingRequest(accountID)
                            } else {
                                Toast.makeText(context, "Failed to accept request...", Toast.LENGTH_SHORT).show()
                                Log.e(
                                    "FriendService",
                                    "Failed to get friends: ${response.message()}"
                                )
                            }
                        }
                    },
                onRejectRequest = {accountID ->
                        viewModel.viewModelScope.launch {
                            val service = serviceFactory.createService(FriendshipService::class.java)
                            val response = service.removeFriends(
                                RemoveFriendBody(
                                    accountID,
                                    sessionToken
                                )
                            )
                            if (response.isSuccessful) {
                                addFriend(accountID)
                                removeIncomingRequest(accountID)
                            } else {
                                Toast.makeText(context, "Failed to reject request...", Toast.LENGTH_SHORT).show()
                                Log.e(
                                    "FriendService",
                                    "Failed to get friends: ${response.message()}"
                                )
                            }
                        }
                    }
                )

            FriendTab.OutgoingRequests -> OutgoingRequestsTabContent(
                outgoingRequests,
                retractRequest = { accountID ->
                    viewModel.viewModelScope.launch {
                        val service = serviceFactory.createService(FriendshipService::class.java)
                        val response = service.removeFriends(
                            RemoveFriendBody(
                                accountID,
                                sessionToken
                            )
                        )
                        if (response.isSuccessful) {
                            removeOutgoingRequest(accountID)
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to retract request...",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                "FriendService",
                                "Failed to get friends: ${response.message()}"
                            )
                        }
                    }
                }
            )
        }
    }
}