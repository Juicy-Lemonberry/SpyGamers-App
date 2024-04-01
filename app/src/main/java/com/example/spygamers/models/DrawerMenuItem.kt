package com.example.spygamers.models

import com.example.spygamers.utils.ImageResource

/**
 * Represents a menu item in the main navigation drawer...
 */
data class DrawerMenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageResource,
    val isSelected: Boolean = false
)