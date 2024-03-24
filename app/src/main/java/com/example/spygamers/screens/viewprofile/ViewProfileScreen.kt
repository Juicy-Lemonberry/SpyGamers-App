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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spygamers.components.EditableField
import com.example.spygamers.components.NonEditableField
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.dialogs.ConfirmDialog
import com.example.spygamers.components.dialogs.EditStringDialog
import com.example.spygamers.components.dialogs.EditTimezoneDialog
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.GamePreference
import com.example.spygamers.models.UserAccount
import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.authentication.AuthenticationService
import com.example.spygamers.services.authentication.FullAccountData
import com.example.spygamers.services.profilechanger.ChangeUsernameBody
import com.example.spygamers.services.profilechanger.CreatePreferenceBody
import com.example.spygamers.services.profilechanger.DeletePreferenceBody
import com.example.spygamers.services.profilechanger.ProfileChangerService
import com.example.spygamers.services.profilesearch.ProfileSearchService
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

private enum class CurrentEditMode(val mode: String) {
    NONE("NONE"),
    USERNAME("USERNAME"),
    EMAIL("EMAIL"),
    TIMEZONE("TIMEZONE"),
    GAME_PREFERENCE("GAME_PREFERENCE")
}

@Composable
private fun MainBody(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isLoading = rememberSaveable() { mutableStateOf(true) }

    // State fields for account properties
    val accountID by viewModel.targetViewingAccountID.collectAsState();
    val username = rememberSaveable() { mutableStateOf("") }
    val email = rememberSaveable() { mutableStateOf("") }
    val timezoneCode = rememberSaveable() { mutableStateOf("") }
    val dateCreated = rememberSaveable() { mutableStateOf(Date()) }

    // State fields to determine if viewing self account or not.
    // (To determine if we should allow edits on profile...)
    val selfAccountID by viewModel.accountID.collectAsState()
    val sessionToken by viewModel.sessionToken.collectAsState()
    val isViewingSelf = accountID == selfAccountID;

    // States for editing the fields.
    val initialEditValue = rememberSaveable { mutableStateOf("") }
    val targetEditMode = rememberSaveable { mutableStateOf(CurrentEditMode.NONE) }

    val deletingPreferenceID = rememberSaveable() { mutableIntStateOf(-1) }
    val deletingPreferenceName = rememberSaveable() { mutableStateOf("") }

    LaunchedEffect(accountID) {
        if (isViewingSelf) {
            val accountData = getSelfProfileInformation(context, sessionToken)
            accountData?.let {
                username.value = it.username
                email.value = it.email
                timezoneCode.value = it.timezoneCode
                dateCreated.value = it.createdAt
            }
        } else {
            val accountData = getPublicProfileInformation(context, accountID)
            accountData?.let {
                username.value = it.username
                timezoneCode.value = it.timezoneCode
                dateCreated.value = it.dateCreated
            }
        }

        val fetchedPreferences = getGamePreferences(context, accountID);
        fetchedPreferences?.let{
            Log.d("fetchedPreferences", "SIZE : ${fetchedPreferences.size}")
            viewModel.setGamePreferences(it)
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
            initialEditValue.value = username.value
            targetEditMode.value = CurrentEditMode.USERNAME
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            item {
                if (isViewingSelf) {
                    // Show editable email field, and timezone code if viewing own profile...
                    NonEditableField("Email", email.value)
                    EditableField("Timezone", timezoneCode.value) {
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
                GamePreferenceSection(
                    isViewingSelf,
                    viewModel.gamePreferences,
                    onAddPreferenceRequest = {
                        targetEditMode.value = CurrentEditMode.GAME_PREFERENCE
                    },
                    onDeletePreferenceRequest = {
                        deletingPreferenceID.intValue = it.id
                        deletingPreferenceName.value = it.name
                        targetEditMode.value = CurrentEditMode.GAME_PREFERENCE
                    }
                )
            }
        }
    }


    if (targetEditMode.value == CurrentEditMode.NONE) {
        return;
    }

    if (targetEditMode.value == CurrentEditMode.GAME_PREFERENCE && deletingPreferenceName.value.isNotEmpty()) {
        ConfirmDialog(textContent = "Deleting Game Preference: $deletingPreferenceName",
            onDismiss = {
                targetEditMode.value = CurrentEditMode.NONE
            },
            onConfirm = {
                coroutineScope.launch {
                    val success = deleteGamePreference(context, sessionToken, deletingPreferenceID.intValue)
                    deletingPreferenceName.value = ""
                    targetEditMode.value = CurrentEditMode.NONE

                    if (success) {
                        viewModel.removeGamePreferenceByID(deletingPreferenceID.intValue)
                    }
                }
            }
        )
    } else if (targetEditMode.value == CurrentEditMode.TIMEZONE) {
        EditTimezoneDialog(targetEditMode.value.mode,
            initialEditValue.value,
            onDismiss = { targetEditMode.value = CurrentEditMode.NONE },
            onSave = {
                timezoneCode.value = it
                targetEditMode.value = CurrentEditMode.NONE
            })
    } else {
        EditStringDialog(
            targetEditMode.value.mode,
            initialEditValue.value,
            onDismiss = { targetEditMode.value = CurrentEditMode.NONE }) { newValue ->
            when (targetEditMode.value.mode) {
                CurrentEditMode.USERNAME.mode -> {
                    coroutineScope.launch {
                        changeUsername(context, sessionToken, newValue)
                        targetEditMode.value = CurrentEditMode.NONE
                    }
                    username.value = newValue
                }

                CurrentEditMode.GAME_PREFERENCE.mode -> {
                    coroutineScope.launch {
                        createGamePreference(context, sessionToken, newValue)
                        targetEditMode.value = CurrentEditMode.NONE
                        fetchPreferences(viewModel, context, accountID)
                    }
                }
            }
        }
    }
}

//#region Utils
// TODO: Refactor...

private suspend fun fetchPreferences(viewModel: GamerViewModel, context: Context, accountID: Int) {
    val fetchedPreferences = getGamePreferences(context, accountID);
    fetchedPreferences?.let{
        Log.d("fetchedPreferences", "SIZE : ${fetchedPreferences.size}")
        viewModel.setGamePreferences(it)
    }
}

private suspend fun changeUsername(context: Context, authToken: String, newUsername: String) {
    val service = ServiceFactory().createService(ProfileChangerService::class.java)
    val response = service.changeUsername(
        ChangeUsernameBody(
            authToken,
            newUsername
        )
    )

    if (!response.isSuccessful) {
        Log.w("ViewProfileScreen", response.errorBody().toString())
        Toast.makeText(context, "Failed to create game preference!", Toast.LENGTH_SHORT).show()
        return
    }

    val responseBody = response.body()!!
    if (responseBody.status != "SUCCESS") {
        Log.w("ViewProfileScreen", "STATUS :: ${responseBody.status}")
        Toast.makeText(context, "Failed to create game preference!", Toast.LENGTH_SHORT).show()
        return
    }
}

private suspend fun createGamePreference(context: Context, authToken: String, preferenceName: String) {
    val service = ServiceFactory().createService(ProfileChangerService::class.java)
    val response = service.createGamePreference(CreatePreferenceBody(
        authToken,
        preferenceName
    )
    )

    if (!response.isSuccessful) {
        Log.w("ViewProfileScreen", response.errorBody().toString())
        Toast.makeText(context, "Failed to create game preference!", Toast.LENGTH_SHORT).show()
    }

    val responseBody = response.body()!!
    if (responseBody.status != "SUCCESS") {
        Log.w("ViewProfileScreen", "STATUS :: ${responseBody.status}")
        Toast.makeText(context, "Failed to create game preference!", Toast.LENGTH_SHORT).show()
    }
}

private suspend fun deleteGamePreference(context: Context, authToken: String, preferenceID: Int): Boolean {
    val service = ServiceFactory().createService(ProfileChangerService::class.java)
    val response = service.deleteGamePreference(DeletePreferenceBody(
        authToken,
        preferenceID
    ))

    if (!response.isSuccessful) {
        Log.w("ViewProfileScreen", response.errorBody().toString())
        Toast.makeText(context, "Failed to delete game preference!", Toast.LENGTH_SHORT).show()
        return false
    }

    val responseBody = response.body()!!
    if (responseBody.status != "SUCCESS") {
        Log.w("ViewProfileScreen", "STATUS :: ${responseBody.status}")
        Toast.makeText(context, "Failed to delete game preference!", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}

private suspend fun getGamePreferences(context: Context, accountID: Int): List<GamePreference>? {
    Log.d("getGamePreferences", "Account ID: $accountID")
    val service = ServiceFactory().createService(ProfileSearchService::class.java)
    val response = service.getGamePreferences(accountID);

    if (!response.isSuccessful) {
        Log.w("ViewProfileScreen", response.errorBody().toString())
        Toast.makeText(context, "Failed to fetch game preferences!", Toast.LENGTH_SHORT).show()
        return null
    }
    Log.d("getGamePreferences", "SUCCESS")

    val responseBody = response.body()!!
    Log.w("ViewProfileScreen", "STATUS :: ${responseBody.status}")
    if (responseBody.status != "SUCCESS") {
        Toast.makeText(context, "Failed to fetch game preferences!", Toast.LENGTH_SHORT).show()
        return null
    }

    return responseBody.preferences;
}

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