package com.example.spygamers.screens.friendlist

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.components.recommendChecker.ContactsChecker
import com.example.spygamers.components.recommendChecker.LocationChecker
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.Friendship
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.friendship.AddFriendBody
import com.example.spygamers.services.friendship.FriendshipService
import com.example.spygamers.services.friendship.RemoveFriendBody
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
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
    val scope = rememberCoroutineScope()
    val accountID by viewModel.accountID.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                appBarTitle = "Friends, and Friend Requests"
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(Screen.FriendListScreen),
                onItemClick = {item ->
                    viewModel.setViewingUserAccount(accountID)
                    handleDrawerItemClicked(item, Screen.FriendListScreen, navController)
                }
            )
        }
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