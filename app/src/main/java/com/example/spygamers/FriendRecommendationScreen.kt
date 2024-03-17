package com.example.spygamers

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun FriendRecommendationScreen(
    navController: NavController,
    viewModel: GamerViewModel
) {
    val auth_token = viewModel.getSessionToken().toString()

    // Maintain a list of friend recommendations
    var recommendations by remember { mutableStateOf<List<result>?>(null) }

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
                    val response = service.getRecommendations(getRecomendation(auth_token))

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

    // Display the list of friend recommendations or a message if empty
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