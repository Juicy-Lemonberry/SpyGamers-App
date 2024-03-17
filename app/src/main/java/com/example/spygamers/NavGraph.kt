package com.example.spygamers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spygamers.controllers.GamerViewModel
import com.example.spygamers.screens.FriendListScreen
import com.example.spygamers.screens.FriendRecommendationScreen
import com.example.spygamers.screens.HomeScreen
import com.example.spygamers.screens.LoginScreen
import com.example.spygamers.screens.RegisterScreen
import com.example.spygamers.screens.SettingScreen

sealed class Screen(val route: String) {
    object LoginScreen : Screen(route = "Login_Screen")
    object RegisterScreen : Screen(route = "Register_Screen")
    object SettingScreen : Screen(route = "Setting_Screen")
    object FriendListScreen : Screen(route = "FriendList_Screen")
    object HomeScreen : Screen(route = "Home_Screen")
    object FriendRecommendationScreen : Screen(route = "FriendRecommendation_Screen")

    object GroupRecommendationScreen : Screen(route = "GroupRecommendation_Screen")
    object ViewProfileScreen : Screen(route = "ViewProfile_Screen")
    object CreateGroupScreen : Screen(route = "CreateGroup_Screen")
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
        composable(route = "FriendList_Screen") {
            FriendListScreen(navController = navController, viewModel)
        }
        composable(route = "FriendRecommendation_Screen") {
            FriendRecommendationScreen(navController = navController, viewModel)
        }
    }
}