package com.example.spygamers.utils

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.spygamers.Screen
import com.example.spygamers.models.DrawerMenuItem

fun handleDrawerItemClicked(drawerItem: DrawerMenuItem, currentDrawerItem: Screen, navController: NavController) {
    if (drawerItem.id === currentDrawerItem.route) {
        return;
    }

    navController.navigate(drawerItem.id)
}