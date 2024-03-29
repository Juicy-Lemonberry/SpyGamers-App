package com.example.spygamers.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.spygamers.Screen
import com.example.spygamers.controllers.GamerViewModel

@Composable
fun InitialScreen (
    navController: NavController,
    viewModel: GamerViewModel
) {
    val isLoading by viewModel.isInitializing.collectAsState()
    // Check if there is a session token, and if its valid.
    // If its valid, move to home screen,
    // if not valid, or not exists, move to login screen...
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color= MaterialTheme.colors.primary)
    }
}