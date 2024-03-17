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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.components.AppBar
import com.example.spygamers.components.DrawerBody
import com.example.spygamers.components.DrawerHeader
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.services.RecommendationService
import com.example.spygamers.services.GetRecommendationBody
import com.example.spygamers.services.RecommendedFriend
import com.example.spygamers.utils.generateDefaultDrawerItems
import com.example.spygamers.utils.handleDrawerItemClicked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
                items = generateDefaultDrawerItems(),
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
    val auth_token = viewModel.getSessionToken().toString()
    // Maintain a list of friend recommendations
    var recommendations by rememberSaveable { mutableStateOf<List<RecommendedFriend>?>(null) }

    // Fetch friend recommendations from API
    LaunchedEffect(Unit) {
        if (auth_token.isNotEmpty()) {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                try {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://spygamers.servehttp.com:44414/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(RecommendationService::class.java)
                    val response = service.getRecommendations(GetRecommendationBody(auth_token))

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            recommendations = responseBody.result
                        } else {
                            Log.e("FriendRecommendationScreen", "Response body is null")
                        }
                    } else {
                        Log.e("FriendRecommendationScreen", "Failed to get recommendations: ${response}")
                    }
                } catch (e: Exception) {
                    Log.e("FriendRecommendationScreen", "Error fetching recommendations", e)
                }
            }
        } else {
            Log.e("FriendRecommendationScreen", "Auth token is empty")
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (recommendations == null) {
            CircularProgressIndicator() // Show loading indicator while fetching recommendations
        } else if (recommendations.isNullOrEmpty()) {
            Text(text = "No Friend Recommendations", modifier = Modifier.padding(horizontal = 16.dp))
        } else {
            LazyColumn {
                items(recommendations!!) { friend ->
                    Text(text = "Friend: ${friend.username}")
                }
            }
        }
    }
}