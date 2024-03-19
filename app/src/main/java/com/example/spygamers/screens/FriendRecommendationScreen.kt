package com.example.spygamers.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.AppBar
import com.example.spygamers.components.DrawerBody
import com.example.spygamers.components.DrawerHeader
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.models.RecommendedFriend
import com.example.spygamers.services.ServiceFactory
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
    var recommendations by rememberSaveable { mutableStateOf<List<RecommendedFriend>>(emptyList()) }
    var isLoading by rememberSaveable {
        mutableStateOf(true)
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator() // Show loading indicator while fetching recommendations
        } else if (recommendations.isEmpty()) {
            Text(text = "No friends Recommended...")
        } else {
            LazyColumn {
                items(recommendations) { friend ->
                    Text(text = "Friend: ${friend.username}")
                }
            }
        }
    }
}