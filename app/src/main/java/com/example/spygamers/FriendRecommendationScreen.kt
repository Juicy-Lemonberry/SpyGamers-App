package com.example.spygamers

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    var recommendations by remember { mutableStateOf<List<Recommendation>>(emptyList()) }

    // Fetch friend recommendations from API
    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://spygamers.servehttp.com:44414/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RecommendationService::class.java)
            val response = service.getRecommendations(getRecomendation(auth_token))

            if (response.isSuccessful) {
                recommendations = response.body()?.friends ?: emptyList()
            } else {
                // Handle error
                // You can show an error message or handle it as per your requirement
            }
        }
    }

    // Display the list of friend recommendations or a message if empty
    if (recommendations.isEmpty()) {
        Text(text = "No Friend Recommendations")
    } else {
        LazyColumn {
            items(recommendations.size) { index ->
                val friend = recommendations[index]
                Text(
                    text = "Friend: ${friend.username}"
                )
            }
        }
    }
}