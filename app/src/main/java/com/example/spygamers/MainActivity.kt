package com.example.spygamers

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.ui.theme.SpyGamersTheme

import com.example.spygamers.controllers.GamerViewModelFactory

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter",
        "UnusedMaterialScaffoldPaddingParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = GamerViewModelFactory(
            (application as GamerApp).repository
        )
        setContent {
            SpyGamersTheme {
                // Initialize NavController and ViewModel here
                val navController = rememberNavController()
                val viewModel: GamerViewModel = viewModel(factory = viewModelFactory)

                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}