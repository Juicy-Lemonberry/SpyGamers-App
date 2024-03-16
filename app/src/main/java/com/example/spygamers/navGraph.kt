package com.example.spygamers

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object LoginScreen : Screen(route = "Login_Screen")
    object RegisterScreen : Screen(route = "Register_Screen")
    object SettingScreen : Screen(route = "Setting_Screen")
    object FriendListScreen : Screen(route = "FriendList_Screen")
    object HomeScreen : Screen(route = "Home_Screen")
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
            LoginScreen(navController = navController, viewModel)
        }
        composable(route = "Register_Screen") {
            RegisterScreen(navController = navController, viewModel)
        }
        composable(route = "Setting_Screen") {
            SettingScreen(navController = navController, viewModel)
        }
        composable(route = "Home_Screen") {
            HomeScreen(navController = navController, viewModel)
        }
    }
}