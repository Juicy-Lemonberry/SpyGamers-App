package com.example.spygamers.screens.groupmessage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.components.chatting.ChatInputRow
import com.example.spygamers.components.chatting.MessageData
import com.example.spygamers.components.chatting.MessageRow
import com.example.spygamers.components.dialogs.ConfirmDialog
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.messaging.GroupMessage
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.group.GroupService
import com.example.spygamers.services.group.response.SendGroupMessageResponse
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GroupMessageScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val accountID by viewModel.accountID.collectAsState()
    val groupName by viewModel.targetGroupName.collectAsState()

    // TODO: On appbar title click, show list of group members...
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                appBarTitle = groupName
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(),
                onItemClick = {item ->
                    viewModel.setViewingUserAccount(accountID)
                    handleDrawerItemClicked(item, Screen.GroupMessageScreen, navController)
                }
            )
        }
    ) {
        MainBody(navController, viewModel)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainBody(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val permissionsState = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val permissionGrantState by viewModel.grantedMediaFileAccess.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val serviceFactory = ServiceFactory()
    val context = LocalContext.current

    val sessionToken by viewModel.sessionToken.collectAsState()
    val currentUsername by viewModel.username.collectAsState()
    val messages = viewModel.groupMessages
    val lazyListState = rememberLazyListState()

    Log.d("GroupMessageScreen", "Message Size: ${messages.size}")

    var requestedForPermissions by rememberSaveable {
        mutableStateOf(false)
    }

    // Request for permission to send attachments...
    if (!requestedForPermissions && !permissionGrantState) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        if (permissionsState.status.isGranted) {
            Log.d("GroupMessageScreen", "All permissions already granted, skipping...")
            // All permissions already granted, no need to request...
            requestedForPermissions = true
            return
        }

        Log.d("GroupMessageScreen", "No permissions granted, requesting...")
        // Popup explain why we need permissions, before requesting...
        ConfirmDialog(
            textContent = "We require permissions to send attachments!",
            onDismiss = {
                Log.d("GroupMessageScreen", "Rejected to give permissions...")
                Toast.makeText(context, "Sending of attachments may not work...", Toast.LENGTH_LONG).show()
                requestedForPermissions = true
            },
            onConfirm = {
                coroutineScope.launch {
                    permissionsState.launchPermissionRequest()
                    requestedForPermissions = true
                    viewModel.updateMediaFileGrants(true)
                }
            }
        )
        return;
    }

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
                val groupMessage = messages[index]
                Log.d("GroupMessageScreen", "INDEX :: $index :: $groupMessage")
                Log.d("GroupMessageScreen.Attachments", "ATTACHMENTS :: ${groupMessage.attachmentsID}")
                val messageData = MessageData(
                    messageID = groupMessage.messageID,
                    authorUsername = groupMessage.senderUsername,
                    content = groupMessage.content,
                    timestamp = groupMessage.timestamp,
                    attachmentsID = groupMessage.attachmentsID,
                    senderIsSelf = groupMessage.senderUsername == currentUsername,
                    isDeletedMessage = groupMessage.isDeleted
                )

                MessageRow(
                    messageData,
                    sessionToken,
                    serviceFactory,
                    context
                )
            }
        }

        ChatInputRow(
            onUserSendMessage = { messageContent, attachmentUri ->
                sendMessageToGroup(
                    viewModel,
                    context,
                    serviceFactory,
                    messageContent,
                    attachmentUri
                )
            }
        )
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

private fun sendMessageToGroup(
    viewModel: GamerViewModel,
    context: Context,
    serviceFactory: ServiceFactory,
    content: String,
    imageUri: Uri?
) {
    var sendImage = imageUri != null
    if (sendImage && !viewModel.grantedMediaFileAccess.value) {
        Toast.makeText(context, "Unable to send image due to insufficient permissions!", Toast.LENGTH_SHORT).show()
        sendImage = false
    }

    // TODO: Allow handling of attachments...
    viewModel.viewModelScope.launch(Dispatchers.IO) {
        try {
            val sessionToken = viewModel.sessionToken.value
            val targetGroupID = viewModel.targetGroupID.value

            val service = serviceFactory.createService(GroupService::class.java)

            var response: Response<SendGroupMessageResponse>
            val authTokenPart = MultipartBody.Part.createFormData("auth_token", sessionToken)
            val groupIDPart = MultipartBody.Part.createFormData("group_id", targetGroupID.toString())
            val contentPart = MultipartBody.Part.createFormData("content", content)

            if (sendImage) {
                // Prepare image part if Uri is not null
                val imagePart = imageUri?.let { uri ->
                    uriToMultipartBodyPart(
                        context,
                        uri,
                        "attachments"
                    )
                }

                response = service.sendMessage(
                    authToken = authTokenPart,
                    targetGroupID = groupIDPart,
                    content = contentPart,
                    attachments = imagePart
                )
            } else {
                response = service.sendMessage(
                    authToken = authTokenPart,
                    targetGroupID = groupIDPart,
                    content = contentPart
                )
            }

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

            // Add this newly created group message to the viewModel data....
            val newGroupMessage = GroupMessage(
                messageID = responseBody.result.messageID,
                senderID = viewModel.accountID.value,
                senderUsername = viewModel.username.value,
                content = content,
                timestamp = responseBody.result.timestamp,
                attachmentsID = responseBody.result.attachmentsID,
                isDeleted = false
            )
            viewModel.addGroupMessage(newGroupMessage);
        } catch (e: Exception) {
            Log.e("DirectMessageScreen.sendMessageToUser", "Error sending message :: ", e)
        }
    }
}

private fun uriToMultipartBodyPart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    return try {
        // Use 'use' function to automatically close the InputStream after operation completes
        val byteArray = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // Convert the InputStream to a ByteArray
            inputStream.readBytes()
        }

        // Proceed only if byteArray is not null
        byteArray?.let {
            // Determine the MIME type for the file
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

            // Convert the ByteArray to a RequestBody
            val requestBody = it.toRequestBody(mimeType.toMediaTypeOrNull())

            // Generate the filename
            val filename = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"

            // Create and return the MultipartBody.Part
            MultipartBody.Part.createFormData(partName, filename, requestBody)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}