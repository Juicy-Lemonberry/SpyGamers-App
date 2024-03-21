package com.example.spygamers.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.R
import com.example.spygamers.Screen
import com.example.spygamers.components.appbar.AppBar
import com.example.spygamers.components.appbar.DrawerBody
import com.example.spygamers.components.appbar.DrawerHeader
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.RecommendedFriend
import com.example.spygamers.services.ServiceFactory
import com.example.spygamers.services.recommendations.FriendRequestBody
import com.example.spygamers.services.recommendations.FriendsRecommendationBody
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FriendRecommendationScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

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
                items = generateDefaultDrawerItems(Screen.FriendRecommendationScreen),
                onItemClick = {item ->
                    handleDrawerItemClicked(item, Screen.FriendRecommendationScreen, navController)
                }
            )
        }
    ) {
        MainBody(viewModel, navController);
    }
}

@Composable
private fun MainBody(viewModel: GamerViewModel, navController: NavController){
    val serviceFactory = ServiceFactory();
    val sessionToken by viewModel.sessionToken.collectAsState()
    // Maintain a list of friend recommendations

    var isLoading by rememberSaveable {
        mutableStateOf(true)
    }
    var recommendations by rememberSaveable { mutableStateOf<List<RecommendedFriend>?>(null) }
    fun removeRecommendation(idToRemove: Int) {
        recommendations = recommendations!!.filter { it.id != idToRemove }
    }
    // Fetch friend recommendations from API
    LaunchedEffect(Unit) {
        if (sessionToken.isBlank()) {
            // TODO: Creating some popup warning and a small delay before routing...
            Log.w("FriendRecommendationScreen", "Auth token is blank...");
            navController.navigate(Screen.LoginScreen.route)
        }

        viewModel.viewModelScope.launch(Dispatchers.IO) {
            try {
                val service = serviceFactory.createRecommendationService()
                val response = service.getRecommendations(FriendsRecommendationBody(sessionToken))

                if (!response.isSuccessful) {
                    Log.e("FriendRecommendationScreen", "Failed to get recommendations :: $response")
                }

                val responseBody = response.body()
                if (responseBody == null) {
                    Log.e("FriendRecommendationScreen", "Response body is null")
                } else {
                    recommendations = responseBody.result;
                }
            } catch (e: Exception) {
                Log.e("FriendRecommendationScreen", "Error fetching recommendations :: ", e)
            } finally {
                isLoading = true;
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        if (recommendations == null) {
            CircularProgressIndicator() // Show loading indicator while fetching recommendations
        } else if (recommendations!!.isEmpty()) {
            Text(text = "No friends Recommended...")
        } else {
            LazyColumn {
                item {
                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Friend Suggestion",
                            style = MaterialTheme.typography.h4,
                            color = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                // Code for incoming requests
                items(if (recommendations!!.isEmpty()) 1 else recommendations!!.size) { index ->
                    if (recommendations!!.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profile picture (dummy)
                            // Dummy profile picture
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.White)
                                    .padding(8.dp)
                            )
                            Text(
                                text = "No Recommendation",
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .fillMaxSize(),
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    } else {
                        val recommendation = recommendations!![index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profile picture (dummy)
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.Magenta)
                                    .padding(8.dp)
                            )

                            // Friend name
                            Text(
                                text = "${recommendation.username}",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp, end = 16.dp),
                                style = MaterialTheme.typography.body1,
                            )

                            // Accept icon
                            IconButton(
                                onClick = { viewModel.viewModelScope.launch {

                                    val service = serviceFactory.createRecommendationService();

                                    val response = service.sendFriendRequest(
                                        FriendRequestBody(
                                            sessionToken,
                                            recommendation.id
                                        )
                                    )
                                    if (response.isSuccessful) {
                                       // Send Request
                                        removeRecommendation(recommendation.id)
                                    } else {
                                        // Handle error
                                        Log.e(
                                            "Friend Recommendation",
                                            "Failed to get Recommendation: ${response.message()}"
                                        )
                                    }
                                } },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(MaterialTheme.colors.primary)
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}