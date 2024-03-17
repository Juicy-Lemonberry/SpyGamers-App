package com.example.spygamers.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.spygamers.utils.ImageResource

/**
 * Represents a menu item in the main navigation drawer...
 */
data class DrawerMenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageResource
)