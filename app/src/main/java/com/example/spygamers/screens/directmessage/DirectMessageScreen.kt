package com.example.spygamers.screens.directmessage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
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
import com.example.spygamers.components.recommendChecker.ContactsChecker
import com.example.spygamers.components.recommendChecker.LocationChecker
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.StatusOnlyResponse
import com.example.spygamers.services.directmessaging.DirectMessagingService
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
fun DirectMessageScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val accountID by viewModel.accountID.collectAsState()
    val targetAccountUsername by viewModel.targetMessagingAccountUsername.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                appBarTitle = "Conversation with $targetAccountUsername"
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = generateDefaultDrawerItems(),
                onItemClick = {item ->
                    viewModel.setViewingUserAccount(accountID)
                    handleDrawerItemClicked(item, Screen.DirectMessageScreen, navController)
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
    val messages = viewModel.directMessages
    val lazyListState = rememberLazyListState()

    var requestedForPermissions by rememberSaveable {
        mutableStateOf(false)
    }

    // Request for permission to send attachments...
    if (!requestedForPermissions && !permissionGrantState) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        if (permissionsState.status.isGranted) {
            Log.d("DirectMessageScreen", "All permissions already granted, skipping...")
            // All permissions already granted, no need to request...
            requestedForPermissions = true
            return
        }

        Log.d("DirectMessageScreen", "No permissions granted, requesting...")
        // Popup explain why we need permissions, before requesting...
        ConfirmDialog(
            textContent = "We require permissions to send attachments!",
            onDismiss = {
                Log.d("DirectMessageScreen", "Rejected to give permissions...")
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
                val currentDM = messages[index]

                Log.d("DirectMessageScreen.Attachments", "ATTACHMENTS :: ${currentDM.attachmentsID}")
                val messageData = MessageData(
                    messageID = currentDM.messageID,
                    authorUsername = currentDM.senderUsername,
                    content = currentDM.content,
                    timestamp = currentDM.timestamp,
                    attachmentsID = currentDM.attachmentsID,
                    senderIsSelf = currentDM.senderUsername == currentUsername,
                    isDeletedMessage = currentDM.isDeleted,
                    isDirectMessage = true
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
            onUserSendMessage = { messageContent, messageImage ->
                sendMessageToUser(
                    viewModel,
                    context,
                    serviceFactory,
                    messageContent,
                    messageImage
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

private fun sendMessageToUser(
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
    Log.d("Image URL", "$imageUri")

    // TODO: Allow handling of attachments...
    viewModel.viewModelScope.launch(Dispatchers.IO) {
        try {
            val sessionToken = viewModel.sessionToken.value
            val targetAccountID = viewModel.targetMessagingAccountID.value

            val service = serviceFactory.createService(DirectMessagingService::class.java)

            var response: Response<StatusOnlyResponse>
            val authTokenPart = MultipartBody.Part.createFormData("auth_token", sessionToken)
            val targetAccountIdPart = MultipartBody.Part.createFormData("target_account_id", targetAccountID.toString())
            val contentPart = MultipartBody.Part.createFormData("content", content)


            if (sendImage) {
                // Prepare image part if Uri is not null
                val imagePart = imageUri?.let { uri ->
                    uriToMultipartBodyPart(context, uri, "attachments")
                }
                response = service.sendDirectMessage(
                    authToken = authTokenPart,
                    targetAccountID = targetAccountIdPart,
                    content = contentPart,
                    attachments = imagePart
                )
            } else {
                response = service.sendDirectMessage(
                    authToken = authTokenPart,
                    targetAccountID = targetAccountIdPart,
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

            // TODO: Instead of re-fetching the messages to hydrate the list each time a new message is sent,
            // make it dynamically populate itself instead...
            viewModel.fetchTargetDirectMessages()
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