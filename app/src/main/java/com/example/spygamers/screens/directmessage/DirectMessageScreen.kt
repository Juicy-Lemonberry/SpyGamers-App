package com.example.spygamers.screens.directmessage

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.components.chatting.ChatInputRow
import com.example.spygamers.components.chatting.MessageData
import com.example.spygamers.components.chatting.MessageRow
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.directmessaging.DirectMessagingService
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DirectMessageScreen(
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
                items = generateDefaultDrawerItems(),
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
) {
    val serviceFactory = ServiceFactory();
    val context = LocalContext.current

    val currentUsername by viewModel.username.collectAsState();
    val messages = viewModel.directMessages
    val lazyListState = rememberLazyListState()

    // TODO: Show something like 'this is the start of ur conversation' if there are no messages...
    // TODO: Implement dynamic loading (load more messages once user hit the top of the messages list...)
    Column {
        // LazyColumn for chat messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)  // Fill the remaining space between headers and Input Row
                .padding(1.dp),
            state = lazyListState
        ) {
            items(messages.size) { index ->
                val currentDM = messages[index];
                val messageData = MessageData(
                    messageID = currentDM.messageID,
                    authorUsername = currentDM.senderUsername,
                    content = currentDM.content,
                    timestamp = currentDM.timestamp,
                    attachmentsID = currentDM.attachmentsID,
                    senderIsSelf = currentDM.senderUsername == currentUsername,
                    isDeletedMessage = currentDM.isDeleted
                )

                MessageRow(messageData)
            }
        }

        ChatInputRow(
            onUserSendMessage = {
                sendMessageToUser(
                    viewModel,
                    context,
                    serviceFactory,
                    it
                )
            }
        );
    }

    // NOTE: Remember to change effect logic when implementing dynamic loading...
    // This effect scrolls the list to the bottom when the list of messages changes
    LaunchedEffect(messages.size) {
        // NOTE: Ensure that the itemID doesn't go negative
        // (occurs if there is no item in the list yet, but somehow got triggered...)
        val itemId = if (messages.size - 1 <= 0)  0 else messages.size - 1
        lazyListState.animateScrollToItem(itemId)
    }
}

private fun sendMessageToUser(
    viewModel: GamerViewModel,
    context: Context,
    serviceFactory: ServiceFactory,
    content: String
) {
    // TODO: Allow handling of attachments...
    viewModel.viewModelScope.launch(Dispatchers.IO) {
        try {
            val sessionToken = viewModel.sessionToken.value
            val targetAccountID = viewModel.accountID.value

            val service = serviceFactory.createService(DirectMessagingService::class.java)
            val response = service.sendDirectMessage(
                authToken = sessionToken,
                targetAccountID = targetAccountID,
                content = content
            )

            if (!response.isSuccessful) {
                Log.e("DirectMessageScreen.sendMessageToUser", "Failed to send message :: $response")
                Toast.makeText(context, "Failed to send message! Try again?", Toast.LENGTH_LONG).show()
                return@launch
            }

            // Fetch body response, ensure its not blank...
            val responseBody = response.body()
            if (responseBody == null) {
                Log.e("DirectMessageScreen.sendMessageToUser", "Response body is null")
                Toast.makeText(context, "Failed to send message! Try again?", Toast.LENGTH_LONG).show()
                return@launch
            }

            // TODO: Instead of re-fetching the messages to hydrate the list each time a new message is sent,
            // make it dynamically populate itself instead...
            viewModel.fetchTargetDirectMessages()
        } catch (e: Exception) {
            Log.e("DirectMessageScreen.sendMessageToUser", "Error sending message :: ", e)
        }
    }
}