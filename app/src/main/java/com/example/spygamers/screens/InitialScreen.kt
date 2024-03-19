package com.example.spygamers.screens

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.controllers.GamerViewModel

@Composable
fun InitialScreen (
    navController: NavController,
    viewModel: GamerViewModel
) {
    val isLoading by viewModel.isInitializing.collectAsState()
    LaunchedEffect(isLoading) {
        if (isLoading){
            return@LaunchedEffect
        }

        val isValid = viewModel.checkTokenValidity()

        if (isValid){
            navController.navigate(Screen.HomeScreen.route)
            return@LaunchedEffect
        }

        viewModel.removeSessionToken()
        navController.navigate(Screen.LoginScreen.route);
    }

    // TODO: Center it, or beatify it...
    CircularProgressIndicator(color= MaterialTheme.colors.primary);
}