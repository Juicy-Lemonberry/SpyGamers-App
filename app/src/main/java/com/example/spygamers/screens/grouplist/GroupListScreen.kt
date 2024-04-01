package com.example.spygamers.screens.grouplist

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.ProfilePictureIcon
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.Group
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.group.GroupService
import com.example.spygamers.services.group.body.GetAccountGroupsBody
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GroupListScreen(
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
                appBarTitle = "Your Groups"
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(Screen.GroupListScreen),
                onItemClick = {item ->
                    viewModel.setViewingUserAccount(accountID)
                    handleDrawerItemClicked(item, Screen.GroupListScreen, navController)
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
    val coroutineScope = rememberCoroutineScope()

    var groups by rememberSaveable { mutableStateOf<List<Group>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val service = serviceFactory.createService(GroupService::class.java)
            val response = service.getAccountGroups(
                GetAccountGroupsBody(
                    authToken = sessionToken,
                )
            )

            if (response.isSuccessful) {
                groups = response.body()?.result ?: emptyList()
            } else {
                // Handle error
                Log.e("FriendService", "Failed to get friends: ${response.message()}")
            }
        }
    }

    if (groups.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = "No groups...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4
            )
        }
        return
    }

    LazyColumn {
        items(groups.size) {index ->
            val currentGroup = groups[index]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // TODO: Replace with actual group picture...
                ProfilePictureIcon()

                // Name of group, with onclick callback to selected...
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        // Set to navigate to group's messaging...
                        viewModel.setTargetGroup(
                            currentGroup.id,
                            currentGroup.name,
                            currentGroup.description,
                            currentGroup.isPublic
                        )

                        navController.navigate(Screen.GroupMessageScreen.route)
                    }
                    .padding(8.dp)
                    .weight(4f)
                ) {
                    // Display group's name, then group's description...
                    Text(
                        text = currentGroup.name,
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentGroup.description,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}