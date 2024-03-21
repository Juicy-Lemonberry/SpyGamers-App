package com.example.spygamers.screens.viewprofile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.components.EditStringDialog
import com.example.spygamers.components.EditTimezoneDialog
import com.example.spygamers.components.EditableField
import com.example.spygamers.components.NonEditableField
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.UserAccount
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.authentication.AuthenticationService
import com.example.spygamers.services.authentication.FullAccountData
import com.example.spygamers.services.profilesearch.ProfileSearchService
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import com.example.spygamers.utils.toTimezoneOffset
import kotlinx.coroutines.launch
import java.text.DateFormat.getDateInstance
import java.util.Date

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ViewProfileScreen(
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
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
    ) {
        MainBody(navController, viewModel);
    }
}

private enum class CurrentEditMode {
    USERNAME,
    EMAIL,
    TIMEZONE,
    GAME_PREFERENCE
}

@Composable
private fun MainBody(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val context = LocalContext.current
    val isLoading = rememberSaveable() { mutableStateOf(true) }

    val accountID by viewModel.targetViewingAccountID.collectAsState();
    val username = rememberSaveable() { mutableStateOf("") }
    val email = rememberSaveable() { mutableStateOf("") }
    val timezoneCode = rememberSaveable() { mutableStateOf("") }
    val dateCreated = rememberSaveable() { mutableStateOf(Date()) }

    val selfAccountID by viewModel.accountID.collectAsState()
    val sessionToken by viewModel.sessionToken.collectAsState()
    val isViewingSelf = accountID == selfAccountID;

    // NOTE: For Editing the fields.
    val showEditDialog = rememberSaveable { mutableStateOf(false) }
    val editingField = rememberSaveable { mutableStateOf("") }
    val initialEditValue = rememberSaveable { mutableStateOf("") }
    val targetEditMode = rememberSaveable { mutableStateOf(CurrentEditMode.USERNAME) }

    LaunchedEffect(accountID) {
        if (isViewingSelf) {
            val accountData = getSelfProfileInformation(context, sessionToken)
            accountData?.let {
                username.value = it.username
                email.value = it.email
                timezoneCode.value = it.timezone_code
                dateCreated.value = it.created_at
            }
        } else {
            val accountData = getPublicProfileInformation(context, accountID)
            accountData?.let {
                username.value = it.username
                timezoneCode.value = it.timezoneCode
                dateCreated.value = it.dateCreated
            }
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return;
    }
    Column {
        StickyHeader(username.value, isViewingSelf) { field ->
            showEditDialog.value = true
            editingField.value = field
            initialEditValue.value = username.value
            targetEditMode.value = CurrentEditMode.USERNAME
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            item {
                if (isViewingSelf) {
                    // Show editable email field, and timezone code if viewing own profile...
                    EditableField("Email", email.value) {
                        showEditDialog.value = true
                        editingField.value = "Email"
                        initialEditValue.value = email.value
                        targetEditMode.value = CurrentEditMode.EMAIL
                    }
                    EditableField("Timezone", timezoneCode.value) {
                        showEditDialog.value = true
                        editingField.value = "Timezone"
                        // TODO: Special edit dialog for timezone....
                        initialEditValue.value = timezoneCode.value
                        targetEditMode.value = CurrentEditMode.TIMEZONE
                    }
                } else {
                    // Cant edit if not viewing own profile...
                    NonEditableField(
                        label = "Timezone",
                        value = toTimezoneOffset(timezoneCode.value)
                    )
                }
                NonEditableField("Date Created", getDateInstance().format(dateCreated.value))
                Divider()
                // TODO: Game Prefences section...
            }
        }
    }


    if (showEditDialog.value) {
        if (targetEditMode.value == CurrentEditMode.TIMEZONE) {
            EditTimezoneDialog(editingField.value,
                initialEditValue.value,
                onDismiss = { showEditDialog.value = false },
                onSave = {
                    timezoneCode.value = it
                    showEditDialog.value = false
                })
        } else {
            EditStringDialog(
                editingField.value,
                initialEditValue.value,
                onDismiss = { showEditDialog.value = false }) { newValue ->
                when (editingField.value) {
                    "Username" -> username.value = newValue
                    "Email" -> email.value = newValue
                }
                showEditDialog.value = false
            }
        }
    }

}

//#region Utils
private suspend fun getSelfProfileInformation(context: Context, authToken: String): FullAccountData? {
    val service = ServiceFactory().createService(AuthenticationService::class.java)
    val response = service.checkAuthentication(AuthOnlyBody(authToken));

    if (!response.isSuccessful) {
        Log.w("ViewProfileScreen", response.errorBody().toString())
        Toast.makeText(context, "Failed to fetch your profile information!", Toast.LENGTH_SHORT).show()
        return null
    }

    val responseBody = response.body()!!
    if (responseBody.status == "BAD_AUTH") {
        Toast.makeText(context, "Authentication Failed, login again!", Toast.LENGTH_SHORT).show()
        // TODO: Redirect to login page and remove tokens...
        return null
    } else if (responseBody.status != "SUCCESS") {
        Log.w("ViewProfileScreen", "STATUS :: ${responseBody.status}")
        Toast.makeText(context, "Failed to fetch profile information!", Toast.LENGTH_SHORT).show()
        return null
    }

    return responseBody.result;
}

private suspend fun getPublicProfileInformation(context: Context, accountID: Int): UserAccount? {
    val service = ServiceFactory().createService(ProfileSearchService::class.java)
    val response = service.getProfileInfo(accountID);

    if (!response.isSuccessful) {
        Log.w("ViewProfileScreen", response.errorBody().toString())
        Toast.makeText(context, "Failed to fetch user profile information!", Toast.LENGTH_SHORT).show()
        return null
    }

    val responseBody = response.body()!!
    if (responseBody.status != "SUCCESS") {
        Log.w("ViewProfileScreen", "STATUS :: ${responseBody.status}")
        Toast.makeText(context, "Failed to fetch target profile information!", Toast.LENGTH_SHORT).show()
        return null
    }

    return responseBody.result;
}
//#endregion