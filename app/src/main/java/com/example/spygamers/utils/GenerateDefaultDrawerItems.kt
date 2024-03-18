package com.example.spygamers.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import com.example.spygamers.R
import com.example.spygamers.Screen
import com.example.spygamers.models.DrawerMenuItem

fun generateDefaultDrawerItems(currentSelectedItem: Screen? = null): List<DrawerMenuItem> {
    return listOf(
        DrawerMenuItem(
            id = Screen.ViewProfileScreen.route,
            title = "View Profile",
            contentDescription = "View Your Own Profile",
            icon = ImageResource.Vector(Icons.Default.Person),
            isSelected = currentSelectedItem?.route === Screen.ViewProfileScreen.route
        ),
        DrawerMenuItem(
            id = Screen.FriendListScreen.route,
            title = "Friends",
            contentDescription = "Go View Your Friend Lists",
            icon = ImageResource.Drawable(R.drawable.ic_friends),
            isSelected = currentSelectedItem?.route === Screen.FriendListScreen.route
        ),
        DrawerMenuItem(
            id = Screen.CreateGroupScreen.route,
            title = "Create Group",
            contentDescription = "Create a New Group",
            icon = ImageResource.Drawable(R.drawable.ic_create_group),
            isSelected = currentSelectedItem?.route === Screen.CreateGroupScreen.route
        ),
        DrawerMenuItem(
            id = Screen.FriendRecommendationScreen.route,
            title = "Recommend Friends",
            contentDescription = "Get Recommended Users to be friends with",
            icon = ImageResource.Drawable(R.drawable.ic_recommend_friends),
            isSelected = currentSelectedItem?.route === Screen.FriendRecommendationScreen.route
        ),
        DrawerMenuItem(
            id = Screen.GroupRecommendationScreen.route,
            title = "Recommend Groups",
            contentDescription = "Get Recommended Groups to join",
            icon = ImageResource.Drawable(R.drawable.ic_recommend_group),
            isSelected = currentSelectedItem?.route === Screen.GroupRecommendationScreen.route
        ),
        DrawerMenuItem(
            id = Screen.SettingScreen.route,
            title = "Settings",
            contentDescription = "Change your settings",
            icon = ImageResource.Vector(Icons.Default.Settings),
            isSelected = currentSelectedItem?.route === Screen.SettingScreen.route
        ),
    )
}