package com.example.spygamers.screens.recommendgroups

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.dialogs.ConfirmDialog
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.RecommendedGroup
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.group.GroupService
import com.example.spygamers.services.group.body.JoinGroupBody
import com.example.spygamers.services.recommendations.GroupRecommendationBody
import com.example.spygamers.services.recommendations.RecommendationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecommendGroupsScreen(
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
                appBarTitle = "Group Suggestions"
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
    ) {
        MainBody(viewModel, navController);
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainBody(viewModel: GamerViewModel, navController: NavController){
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS
        )
    )
    val recommendationGrantsState by viewModel.grantedRecommendationsTracking.collectAsState()

    val context = LocalContext.current
    val serviceFactory = ServiceFactory()
    val sessionToken by viewModel.sessionToken.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var recommendedGroups by rememberSaveable { mutableStateOf<List<RecommendedGroup>>(emptyList()) }

    var isLoading by rememberSaveable {
        mutableStateOf(true)
    }

    var requestedForPermissions by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (sessionToken.isBlank()) {
            // TODO: No session token, warn and redirect to login page...
            Log.w("FriendRecommendationScreen", "Auth token is blank...");
            navController.navigate(Screen.LoginScreen.route)
        }

        // API Call to fetch list of recommendations from backend API
        coroutineScope.launch {
            try {
                val service = serviceFactory.createService(RecommendationService::class.java)
                val response = service.getGroupRecommendations(GroupRecommendationBody(sessionToken, chunkSize = 25))

                if (!response.isSuccessful) {
                    Log.e("RecommendGroupsScreen", "Failed to get recommendations :: $response")
                    Toast.makeText(context, "Failed to fetch recommendations!", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Fetch body response, ensure its not blank...
                val responseBody = response.body()
                if (responseBody == null) {
                    Log.e("RecommendGroupsScreen", "Response body is null")
                    Toast.makeText(context, "Failed to fetch recommendations!", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Set recommendations in viewModel...
                recommendedGroups = responseBody.result
                isLoading = false;
            } catch (e: Exception) {
                Log.e("RecommendGroupsScreen", "Error fetching recommendations :: ", e)
            }
        }
    }

    // Request for permission under the pretense that it will help to find better recommendations...
    if (!requestedForPermissions && !recommendationGrantsState) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        if (permissionsState.allPermissionsGranted) {
            Log.d("FriendRecommendationScreen", "All permissions already granted, skipping...")
            // All permissions already granted, no need to request...
            requestedForPermissions = true
            return
        }

        Log.d("FriendRecommendationScreen", "No permissions granted, requesting...")
        // Popup explain why we need permissions, before requesting...
        ConfirmDialog(
            textContent = "We require permissions to find better group recommendations for you!",
            onDismiss = {
                Log.d("FriendRecommendationScreen", "Rejected to give permissions...")
                Toast.makeText(context, "Recommendations might not be the best due to lack of permissions...", Toast.LENGTH_LONG).show()
                requestedForPermissions = true
            },
            onConfirm = {
                coroutineScope.launch {
                    permissionsState.launchMultiplePermissionRequest()
                    requestedForPermissions = true
                    viewModel.updateRecommendationsGrants(true)
                }
            }
        )
        return;
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return;
    }

    // Empty recommendation, display no recommendations text....
    if (recommendedGroups.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = "No groups to recommend...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        LazyColumn {
            // Loop through each recommendation,
            // creating a row of recommendation...
            items(recommendedGroups.size) { index ->
                val recommendation = recommendedGroups[index]
                GroupRecommendationRow(
                    recommendation,
                    onJoinRequest =  {

                        // Request to join group, then navigate to group itself...
                        coroutineScope.launch {
                            val service = serviceFactory.createService(GroupService::class.java);
                            val response = service.joinGroup(
                                JoinGroupBody(
                                    sessionToken,
                                    recommendation.id
                                )
                            )

                            if (!response.isSuccessful) {
                                Log.e(
                                    "RecommendGroupScreen",
                                    "Failed to join group :: ${response.message()}"
                                )
                                Toast.makeText(context, "Failed to join group!", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            // Prep view model to view the group message itself...
                            viewModel.setTargetGroup(
                                it.id,
                                it.name,
                                it.description,
                                true
                            )

                            // Successfully joined group, navigate to group messaging...
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Successfully joined group!\r\nNavigating to group chat...", Toast.LENGTH_LONG).show()
                                delay(750)
                                navController.navigate(Screen.GroupMessageScreen.route)
                            }
                        }
                    }
                )
            }
        }
    }
}