package com.example.spygamers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = GamerViewModel.GamerViewModelFactory(
            (application as GamerApp).repository
        )
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val viewModel: GamerViewModel = viewModel(factory = viewModelFactory)

                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}