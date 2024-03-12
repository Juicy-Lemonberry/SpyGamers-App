package com.example.spygamers

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object LoginScreen : Screen(route = "Login_Screen")
    object RegisterScreen : Screen(route = "Register_Screen")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: GamerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = "Login_Screen") {
            LoginScreen(navController = navController)
        }
        composable(route = "Register_Screen") {
            RegisterScreen(navController = navController, viewModel)
        }
    }
}